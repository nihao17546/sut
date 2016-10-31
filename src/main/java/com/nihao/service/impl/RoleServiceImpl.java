package com.nihao.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nihao.dao.RoleMapper;
import com.nihao.model.Role;
import com.nihao.model.dto.User2RoleDTO;
import com.nihao.model.view.ResourceVO;
import com.nihao.model.view.RoleVO;
import com.nihao.service.ResourceServiceI;
import com.nihao.service.RoleServiceI;
import com.nihao.util.InsertSort;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleServiceImpl implements RoleServiceI{
	
	@Autowired
	private RoleMapper roleMapper;
//	@Autowired
//	private ResourceServiceI resourceService;
//
//	@Override
//	public List<RoleVO> selectListByUserId(Integer userId) {
//		List<Role> list=roleMapper.selectListByUserId(userId);
//		List<RoleVO> voList=new ArrayList<>();
//		for(Role role:list){
//			List<ResourceVO> reList=resourceService.selectListByRoleId(role.getId());
//			RoleVO vo=new RoleVO(role);
//			vo.setResources(reList);
//			voList.add(vo);
//		}
//		InsertSort.sort(voList);
//		return voList;
//	}

	@Override
	public List<RoleVO> selectListByUserId(Integer userId) {
		List<Role> list=roleMapper.selectListByUserId(userId);
		List<RoleVO> voList=new ArrayList<>(list.size());
		for(Role role:list){
			voList.add(new RoleVO(role));
		}
		Collections.sort(voList);
		return voList;
	}

	@Override
	@Transactional
	public void updateUser2Role(Integer userId,Integer[] roleIds) {
		roleMapper.deleteUser2RoleByUserId(userId);
		if(roleIds.length==0)
			return;
		List<User2RoleDTO> list=new ArrayList<>(roleIds.length);
		for(Integer roleId:roleIds){
			User2RoleDTO user2RoleDTO=new User2RoleDTO();
			user2RoleDTO.setUserId(userId);
			user2RoleDTO.setRoleId(roleId);
			list.add(user2RoleDTO);
		}
		roleMapper.batchInsertUser2Role(list);
	}

	@Override
	public int update(Role role) {
		return roleMapper.update(role);
	}

	@Override
	@Transactional
	public int deleteById(Integer id) {
		roleMapper.deleteRole2ResourceByRoleId(id);
		roleMapper.deleteUser2RoleByRoleId(id);
		return roleMapper.deleteById(id);
	}

	@Override
	public Role selectByRolename(String rolename) {
		return roleMapper.selectByRolename(rolename);
	}

	@Override
	@Transactional
	public int saveRole(Role role) throws Exception {
		if(Strings.isNullOrEmpty(role.getRolename()))
			throw new Exception("角色名称不能为空");
		Role check=roleMapper.selectByRolename(role.getRolename());
		if(check!=null)
			throw new Exception("角色名称已存在");
		if("".equals(role.getIconcls()))
			role.setIconcls(null);
		if("".equals(role.getDescription()))
			role.setDescription(null);
		int i=roleMapper.insert(role);
		if(i!=1)
			throw new Exception("新增失败,影响条目数:"+i);
		return i;
	}

}
