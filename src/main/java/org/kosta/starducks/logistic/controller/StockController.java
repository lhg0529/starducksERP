package org.kosta.starducks.logistic.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosta.starducks.commons.menus.MenuService;
import org.kosta.starducks.generalAffairs.entity.Product;
import org.kosta.starducks.generalAffairs.service.ProductService;
import org.kosta.starducks.logistic.entity.StoreInbound;
import org.kosta.starducks.logistic.entity.StoreInventory;
import org.kosta.starducks.logistic.entity.StoreInventoryId;
import org.kosta.starducks.logistic.service.StoreInventoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/logistic/stock")
@RequiredArgsConstructor
@Slf4j

public class StockController {


    private final ProductService productService;
    private final StoreInventoryService storeInventoryService;
    private final HttpServletRequest request;
    
    @GetMapping("/warehouse/list")
    public String getAllInventories(Model m,
                                    @PageableDefault(page = 0, size=5, sort = "productCode", direction = Sort.Direction.DESC) Pageable pageable,
                                    @RequestParam(name="searchKeyword", required = false) String searchKeyword)
    {

        Page<Product> allInventory = null;
        if(searchKeyword == null){
            allInventory = productService.getAllProducts(pageable);

        }
        else{
            allInventory = productService.productSearchList(searchKeyword,pageable);

        }

        int nowPage = allInventory.getPageable().getPageNumber()+1;
        //pageable에서 넘어온 현재 페이지를 가져온다.
        int startPage= Math.max(nowPage-4,1);
        int endPage= Math.min(nowPage+5,allInventory.getTotalPages());

        m.addAttribute("products", allInventory);
        m.addAttribute("nowPage",nowPage);
        m.addAttribute("startPage",startPage);
        m.addAttribute("endPage",endPage);
        return "logistic/InventoryList";
    }


    @GetMapping("/warehouse/info/{code}")
    public String getwInventoryInfo(
            @PathVariable("code") Long productCode,
            Model m)
    {
        Optional<Product> product = productService.getProduct(productCode);
        m.addAttribute("inventory",product.get());

        return "logistic/InventoryDetail";
    }

    @GetMapping("/store/list")
    public String getAllInventories(Model m)
    {
        List<StoreInventory> allInventories = storeInventoryService.getAllInventories();

        m.addAttribute("inventories", allInventories);
        return "logistic/StoreInventoryList";

    }


    @GetMapping("/store/info/{productCode}/{storeNo}")
    public String getsInventoryInfo(@PathVariable("storeNo") Long storeNo,
                                   @PathVariable("productCode") Long productCode,
                                   Model m)
    {
        StoreInventory inventoryByCodeAndNo = storeInventoryService.getInventoryByNoAndCode(storeNo,productCode);
        m.addAttribute("inventory",inventoryByCodeAndNo);

        return "logistic/StoreInventoryDetail";
    }



    //재고 목록 조회
//    @GetMapping("/list1")
//    public String getAllInventories(Model m,
//                                 @PageableDefault(page = 0, size=3, sort = "productCode", direction = Sort.Direction.DESC) Pageable pageable,
//                                 @RequestParam(name="searchKeyword", required = false) String searchKeyword)
//    {
//        Page<StoreInventory> allInventory = null;
//        if(searchKeyword == null){
//            allInventory = productService.getAllProducts(pageable);
//
//        }
//        else{
//            allInventory = productService.productSearchList(searchKeyword,pageable);
//
//        }
//
//        int nowPage = allInventory.getPageable().getPageNumber()+1;
//        //pageable에서 넘어온 현재 페이지를 가져온다.
//        int startPage= Math.max(nowPage-4,1);
//        int endPage= Math.min(nowPage+5,allInventory.getTotalPages());
//
//        m.addAttribute("products", allInventory);
//        m.addAttribute("nowPage",nowPage);
//        m.addAttribute("startPage",startPage);
//        m.addAttribute("endPage",endPage);
//        return "logistic/InventoryList";
//    }




}
