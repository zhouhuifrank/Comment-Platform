package com.frankzhou.comment;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frankzhou.comment.dto.CatalogTreeDTO;
import com.frankzhou.comment.entity.ShopCatalog;
import com.frankzhou.comment.mapper.ShopCatalogMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 店铺目录树测试
 * @date 2023-03-23
 */
@Slf4j
@SpringBootTest
public class CatalogTreeTests {

    @Resource
    private ShopCatalogMapper catalogMapper;

    @Test
    public List<ShopCatalog> queryChildNode() {
        LambdaQueryWrapper<ShopCatalog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(ShopCatalog::getTreePath,"/root/%");
        List<ShopCatalog> shopCatalogList = catalogMapper.selectList(wrapper);
        shopCatalogList.forEach(item -> {
            System.out.println(item);
        });

        return shopCatalogList;
    }

    @Test
    public CatalogTreeDTO buildCatalogTree() {
        LambdaQueryWrapper<ShopCatalog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShopCatalog::getLevel,1).eq(ShopCatalog::getStatus,"NORMAL");
        ShopCatalog catalogRoot = catalogMapper.selectOne(wrapper);

        CatalogTreeDTO catalogTree = new CatalogTreeDTO();
        // 保存根结点
        catalogTree.setId(catalogRoot.getId());
        catalogTree.setParentCode(catalogRoot.getParentCode());
        catalogTree.setLevel(catalogRoot.getLevel());
        catalogTree.setTreePath(catalogRoot.getTreePath());
        catalogTree.setSortNum(catalogRoot.getSortNum());

        Queue<CatalogTreeDTO> catalogQueue = new LinkedList<>();
        catalogQueue.offer(catalogTree);
        Integer currLevel = 2;
        while (!catalogQueue.isEmpty()) {
            // 查询当前层级的所有子结点 currLevel
            int currSize = catalogQueue.size();
            for (int i=0;i<currSize;i++) {
                CatalogTreeDTO currNode = catalogQueue.poll();
                // 查询下一层的子结点
                String fuzzyPath = currNode.getTreePath() + "/%";
                LambdaQueryWrapper<ShopCatalog> pathWrapper = new LambdaQueryWrapper<>();
                pathWrapper.like(ShopCatalog::getTreePath,fuzzyPath)
                        .eq(ShopCatalog::getStatus,"NORMAL")
                        .eq(ShopCatalog::getLevel,currLevel);
                List<ShopCatalog> childNode = catalogMapper.selectList(pathWrapper);
                List<CatalogTreeDTO> treeDtoList = new ArrayList<>();
                childNode.forEach(item -> {
                    CatalogTreeDTO dto = new CatalogTreeDTO();
                    BeanUtil.copyProperties(item,dto);
                    treeDtoList.add(dto);
                    catalogQueue.offer(dto);
                });
                currNode.setChildrenNodeList(treeDtoList);
            }
            currLevel++;
        }

        return catalogTree;
    }
}
