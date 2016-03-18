package com.udfex.ams.module.account.web.controller;

import com.udfex.ams.module.account.model.User;
import com.udfex.ams.module.account.service.RoleService;
import com.udfex.ams.module.account.service.UserService;
import com.udfex.ams.module.account.web.controller.vo.CreateUserGroup;
import com.udfex.ams.module.account.web.controller.vo.UpdateUserGroup;
import com.udfex.ams.module.account.web.controller.vo.UserGroupRelation;
import com.udfex.ucs.module.user.entity.SysRoles;
import com.xmomen.framework.mybatis.page.Page;
import com.xmomen.framework.web.exceptions.ArgumentValidException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by Jeng on 2016/1/5.
 */
@RestController
public class UserGroupController {

    @Autowired
    RoleService roleService;
    @Autowired
    UserService userService;

    /**
     * 查询用户组列表
     * @param limit
     * @param offset
     * @param keyword
     * @return
     */
    @RequestMapping(value = "/group", method = RequestMethod.GET)
    public Page<SysRoles> getUserList(@RequestParam(value = "limit") Integer limit,
                                  @RequestParam(value = "offset") Integer offset,
                                  @RequestParam(value = "keyword", required = false) String keyword){
        keyword = StringUtils.trimToEmpty(keyword);
        return roleService.findRoles("%" + keyword + "%", limit, offset);
    }

    /**
     *  查询用户组
     * @param id
     */
    @RequestMapping(value = "/group/{id}", method = RequestMethod.GET)
    public SysRoles getUserList(@PathVariable(value = "id") Integer id){
        return roleService.getRole(id);
    }

    /**
     * 新增用户组
     * @param createUserGroup
     * @param bindingResult
     * @throws ArgumentValidException
     */
    @RequestMapping(value = "/group", method = RequestMethod.POST)
    public void createUser(@RequestBody @Valid CreateUserGroup createUserGroup, BindingResult bindingResult) throws ArgumentValidException {
        if(bindingResult != null && bindingResult.hasErrors()){
            throw new ArgumentValidException(bindingResult);
        }
        SysRoles sysRoles = new SysRoles();
        sysRoles.setRole(createUserGroup.getUserGroup());
        sysRoles.setDescrption(createUserGroup.getDescription());
        sysRoles.setAvailable(createUserGroup.getAvailable() != null && createUserGroup.getAvailable() == 1 ? 1 : 0);
        roleService.createRole(sysRoles);
    }

    /**
     *  删除用户组
     * @param id
     */
    @RequestMapping(value = "/group/{id}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable(value = "id") Integer id){
        roleService.deleteRole(id);
    }

    /**
     *  修改用户组
     * @param id
     */
    @RequestMapping(value = "/group/{id}", method = RequestMethod.PUT)
    public void updateUserGroup(@PathVariable(value = "id") Integer id,
                           @RequestBody @Valid UpdateUserGroup updateUserGroup, BindingResult bindingResult) throws ArgumentValidException {
        if(bindingResult != null && bindingResult.hasErrors()){
            throw new ArgumentValidException(bindingResult);
        }
        SysRoles sysRoles = new SysRoles();
        sysRoles.setId(id);
        sysRoles.setDescrption(updateUserGroup.getDescription());
        if(updateUserGroup.getAvailable() != null){
            sysRoles.setAvailable(updateUserGroup.getAvailable() ? 1 : 0);
        }
        if(updateUserGroup.getUserIdList() != null && updateUserGroup.getUserIdList().size() > 0){
            roleService.updateRole(sysRoles);
        }else{
            roleService.updateRole(sysRoles, updateUserGroup.getUserIdList());
        }
    }

    /**
     * 查询用户组用户
     * @param groupId
     * @param limit
     * @param offset
     * @return
     */
    @RequestMapping(value = "/group/{groupId}/user")
    public Page<UserGroupRelation> findUsersByGroup(@PathVariable(value = "groupId") String groupId,
                                       @RequestParam(value = "chose") Boolean unChose,
                                       @RequestParam(value = "limit") Integer limit,
                                       @RequestParam(value = "offset") Integer offset){
        if(unChose == null){
            unChose = false;
        }
        return roleService.findUsersByRoles(groupId, unChose, limit, offset);
    }

    /**
     * 绑定用户
     * @param groupId
     * @param chose
     * @param userId
     */
    @RequestMapping(value = "/group/{groupId}/user", method = RequestMethod.PUT)
    public void findUsersByGroup(@PathVariable(value = "groupId") Integer groupId,
                                 @RequestParam(value = "chose") Boolean chose,
                                 @RequestParam(value = "userId") Integer userId){
        if(chose != null && chose){
            userService.correlationRoles(userId, groupId);
        }else{
            userService.uncorrelationRoles(userId, groupId);
        }
    }

}