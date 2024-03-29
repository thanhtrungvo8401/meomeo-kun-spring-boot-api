package fusikun.com.api.service;

import java.util.*;
import java.util.stream.Collectors;

import fusikun.com.api.dtoREQ.MenuRequest;
import fusikun.com.api.dtoRES.MenuResponse;
import fusikun.com.api.dtoRES.ObjectsManagementList;
import fusikun.com.api.enums.UrlEnpointEnums;
import fusikun.com.api.exceptionHandlers.Ex_MethodArgumentNotValidException;
import fusikun.com.api.utils.Constant;
import fusikun.com.api.validator.MenuDataValidate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fusikun.com.api.dao.MenuRepository;
import fusikun.com.api.model.app.Menu;
import fusikun.com.api.specificationSearch.Specification_Menu;
import fusikun.com.api.specificationSearch._SearchCriteria;
import fusikun.com.api.enums.SearchOperator;

@Service
@Transactional(rollbackFor = Exception.class)
public class MenuService {
    @Autowired
    MenuRepository menuRepository;

    @Autowired
    MenuDataValidate menuDataValidate;

    public Menu findById(UUID id) {
        Optional<Menu> optMenu = menuRepository.findById(id);
        return optMenu.orElse(null);
    }

    public Menu findByName(String name) {
        List<Menu> list = menuRepository.findByName(name);
        if (!list.isEmpty())
            return list.get(0);
        else
            return null;
    }

    public Menu save(Menu entity) {
        if (entity.getId() == null) {
            entity.setCreatedDate(new Date());
        }
        entity.setUpdatedDate(new Date());
        if (entity.getIsActive() == null) {
            entity.setIsActive(true);
        }
        return menuRepository.save(entity);
    }

    public List<Menu> findAll() {
        return menuRepository.findAll();
    }

    public List<Menu> findNotMappedMenus(List<String> listName) {
        Specification_Menu menuSpecification = new Specification_Menu();
        menuSpecification.add(new _SearchCriteria("name", SearchOperator.NOT_IN, listName));
        return menuRepository.findAll(menuSpecification);
    }

    public Boolean existsById(UUID id) {
        return menuRepository.existsById(id);
    }

    public Boolean existsByName(String name) {
        return this.countByName(name) > 0;
    }

    public Integer countByName(String name) {
        return menuRepository.countByName(name);
    }

    public MenuActionsMangement _findAllMenuActions() {
        List<Menu> menus = menuRepository.findAll();
        Long total = menuRepository.count();
        List<MenuResponse> menuResponses = menus.stream().map(MenuResponse::new).collect(Collectors.toList());
        return new MenuActionsMangement(menuResponses, total);
    }

    public void _generateActionsMenu() throws Ex_MethodArgumentNotValidException {
        // Delete
        List<String> listName = new ArrayList<>();
        for (UrlEnpointEnums enpoint : UrlEnpointEnums.values()) {
            listName.add(enpoint.toString());
        }
        Specification_Menu menuSpecification = new Specification_Menu();
        menuSpecification.add(new _SearchCriteria("name", SearchOperator.NOT_IN, listName));
        List<Menu> menusNotInList = menuRepository.findAll(menuSpecification);
        menuRepository.deleteAll(menusNotInList);
        // Create:
        for (UrlEnpointEnums enpoint : UrlEnpointEnums.values()) {
            String enpointName = enpoint.toString();
            String enpointUrl = enpoint.getUrl();
            String enpointRegex = enpoint.getRegex();
            String enpointMethod = enpoint.getMethod();
            MenuRequest menuRequest = new MenuRequest();
            menuRequest.setName(enpointName);
            menuRequest.setUrl(enpointUrl);
            menuRequest.setRegex(enpointRegex);
            menuRequest.setMethod(enpointMethod);
            if (!existsByName(enpointName)) {
                if (enpointName.contains(Constant.MENU_ADDRESS_DEVIDE)) {
                    String parentName = enpointName.split(Constant.MENU_ADDRESS_DEVIDE)[0];
                    Menu parentMenu = findByName(parentName);
                    menuRequest.setParentId(parentMenu.getId());
                }
                menuDataValidate.validate(menuRequest);
                Menu menu = menuRequest.getMenu();
                save(menu);
            }
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private class MenuActionsMangement extends ObjectsManagementList<MenuResponse> {
        MenuActionsMangement(List<MenuResponse> list, Long total) {
            super(list, total);
        }
    }
}
