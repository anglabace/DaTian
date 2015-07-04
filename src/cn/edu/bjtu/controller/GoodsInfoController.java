package cn.edu.bjtu.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import cn.edu.bjtu.bean.search.CargoSearchBean;
import cn.edu.bjtu.service.FocusService;
import cn.edu.bjtu.service.GoodsInfoService;
import cn.edu.bjtu.util.Constant;
import cn.edu.bjtu.util.DownloadFile;
import cn.edu.bjtu.util.PageUtil;
import cn.edu.bjtu.util.UploadPath;
import cn.edu.bjtu.vo.GoodsClientView;

import com.alibaba.fastjson.JSONArray;

@Controller
public class GoodsInfoController {

	@Resource
	GoodsInfoService goodsInfoService;
	@Autowired
	FocusService focusService;
	ModelAndView mv = new ModelAndView();

	/**
	 * 资源栏货物
	 * @param flag
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/goodsform",params="flag=0")
	public String getAllGoodsInfo() {
		return "resource_list6";
	}
	
	/**
	 * 资源栏-货物筛选
	 * @param cargoBean
	 * @param pageUtil
	 * @param session
	 * @return
	 */
	@RequestMapping(value="getSelectedCargoAjax",produces="text/html;charset=UTF-8")
	@ResponseBody
	public String getSelectedCargoAjax(CargoSearchBean cargoBean,PageUtil pageUtil,HttpSession session){
		JSONArray jsonArray = goodsInfoService.getSelectedCargoNew(cargoBean, pageUtil,
				session);
		
		return jsonArray.toString();
	}
	/**
	 * 返回货物筛选页面总记录数
	 * @param warehouseBean
	 * @return
	 */
	@RequestMapping("getSelectedCargoTotalRowsAjax")
	@ResponseBody
	public Integer getSelectedCargoTotalRowsAjax(CargoSearchBean cargoBean){
		Integer count=goodsInfoService.getSelectedCargoTotalRows(cargoBean);
		return count;
	}
	
	/**
	 * 我的信息-货物信息
	 * @param flag
	 * @param request
	 * @return
	 */
	@Deprecated
	@RequestMapping(value="/goodsform",params="flag=1")
	public ModelAndView getMyInfoGoods(@RequestParam int flag,
			HttpSession session) {
			String clientId = (String) session.getAttribute(
					"userId");
			List goodsList = goodsInfoService.getUserGoodsInfo(clientId);
			mv.addObject("goodsList", goodsList);
			mv.setViewName("mgmt_r_cargo");

		return mv;
	}

	@RequestMapping("/goodsdetail")
	/**
	 * 资源栏货物详情
	 * @param id
	 * @return
	 */
	public ModelAndView getAllGoodsDetail(@RequestParam String id) {
		GoodsClientView goodsformInfo = goodsInfoService.getAllGoodsDetail(id);
		mv.addObject("goodsformInfo", goodsformInfo);
		mv.setViewName("resource_detail6");

		return mv;
	}

	@RequestMapping("goodsformselected")
	/**
	 *  * 获取满足条件的货物
	 * @param startPlace
	 * @param endPlace
	 * @param transportType
	 * @param Display
	 * @param PageNow
	 * @param request
	 * @param response
	 * @return
	 */
	@Deprecated
	public ModelAndView getSelectedGoodsInfo(@RequestParam String startPlace,
			@RequestParam String endPlace, @RequestParam String transportType,
			@RequestParam int Display, @RequestParam int PageNow,
			HttpServletRequest request, HttpServletResponse response) {

		try {
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// 
			e.printStackTrace();
		}

		List goodsInfoList = goodsInfoService.getSelectedGoodsInfo(startPlace,
				endPlace, transportType, Display, PageNow);
		int count = goodsInfoService.getTotalRows(startPlace, endPlace,
				transportType);// 获取总记录数

		int pageNum = (int) Math.ceil(count * 1.0 / Display);// 页数
		// System.out.println("总记录数+"+count);
		// System.out.println("页数+"+pageNum);
		mv.addObject("goodsformInfo", goodsInfoList);
		mv.addObject("count", count);
		mv.addObject("pageNum", pageNum);
		mv.addObject("pageNow", PageNow);
		mv.setViewName("resource_list6");

		return mv;
	}

	@RequestMapping(value = "insertGoods", method = RequestMethod.POST)
	public ModelAndView insertGoods(@RequestParam MultipartFile file,
			@RequestParam String name, @RequestParam String type,
			@RequestParam float weight, @RequestParam String transportType,
			@RequestParam String transportReq, @RequestParam String startPlace,
			@RequestParam String endPlace, @RequestParam String damageReq,
			@RequestParam String VIPService,
			@RequestParam(required = false) String VIPServiceDetail,
			@RequestParam String oriented,
			@RequestParam String limitDate, @RequestParam String invoice,
			@RequestParam(required = false) String relatedMaterial,
			@RequestParam String remarks, HttpServletRequest request,
			HttpServletResponse response) {
		System.out.println("进入货物控制器");

		String clientId = (String) request.getSession().getAttribute(Constant.USER_ID);
		String path = null;
		String fileName = null;
		if (file.getSize() != 0)// 有上传文件的情况
		{
			path = UploadPath.getGoodsPath();// 不同的地方取不同的上传路径
			fileName = file.getOriginalFilename();
			fileName = clientId + "_" + fileName;// 文件名
			File targetFile = new File(path, fileName);
			try { // 保存 文件
				file.transferTo(targetFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 没有上传文件的情况path 和 filenName默认为null


		boolean flag = goodsInfoService.insertGoods(name, type, weight,
				transportType, transportReq, startPlace, endPlace, damageReq,
				VIPService, oriented, limitDate, invoice, remarks, clientId,
				path, fileName);
		if (flag == true) {
			try {
				response.sendRedirect("goodsform?flag=1");
			} catch (IOException e) {
				// 
				e.printStackTrace();
			}
		}
		return mv;
	}

	@RequestMapping("getallresponse")
	/**
	 * 获取所有反馈
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getAllResponse(HttpServletRequest request,
			HttpServletResponse response) {
		String userId = (String) request.getSession().getAttribute(Constant.USER_ID);

		List responseList = goodsInfoService.getAllResponse(userId);
		//responseList中的id是goodsid

		mv.addObject("responseList", responseList);
		mv.setViewName("mgmt_d_response");
		return mv;
	}

	@RequestMapping("getresponseform")
	/**
	 * 获取创建反馈表单
	 * @param goodsid
	 * @return
	 */
	public ModelAndView getResponseForm(String goodsid) {
		mv.addObject("goodsId", goodsid);

		mv.setViewName("mgmt_d_response2");

		return mv;
	}

	@RequestMapping("commitresponse")
	/**
	 * 创建反馈
	 * @param goodsid
	 * @return
	 */
	public ModelAndView commitResponse(MultipartFile file,String goodsid, String remarks,
			HttpServletRequest request, HttpServletResponse response) {

		String carrierId = (String) request.getSession().getAttribute(Constant.USER_ID);
		String path = null;
		String fileName = null;
		if (file.getSize() != 0)// 有上传文件的情况
		{
			path = UploadPath.getResponsePath();// 不同的地方取不同的上传路径
			fileName = file.getOriginalFilename();
			fileName = carrierId + "_" + fileName;// 文件名
			File targetFile = new File(path, fileName);
			try { // 保存 文件
				file.transferTo(targetFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
		//没有上传文件的情况path 和 filenName默认为null
		boolean flag = goodsInfoService.commitResponse(goodsid, remarks,
				carrierId,path,fileName);
		if (flag == true) {
			try {
				response.sendRedirect("getallresponse");
			} catch (IOException e) {
				// 
				e.printStackTrace();
			}
		}

		return mv;
	}

	@RequestMapping("mygoodsdetail")
	public ModelAndView myGoodsDetail(@RequestParam String id,
			@RequestParam int flag, HttpServletRequest request,
			HttpServletResponse response) {
		String clientId = (String) request.getSession().getAttribute(Constant.USER_ID);
		GoodsClientView goodsformInfo = goodsInfoService.getAllGoodsDetail(id);
		mv.addObject("goodsdetail", goodsformInfo);

		if (flag == 1) {
			mv.setViewName("mgmt_r_cargo4");
		}

		else if (flag == 2) {
			mv.setViewName("mgmt_r_cargo3");
		}

		return mv;
	}

	@RequestMapping(value = "updategoods", method = RequestMethod.POST)
	public ModelAndView updateGoods(@RequestParam MultipartFile file,
			@RequestParam String id,
			@RequestParam String name, @RequestParam String type,
			@RequestParam float weight, @RequestParam String transportType,
			@RequestParam String transportReq, @RequestParam String startPlace,
			@RequestParam String endPlace, @RequestParam String damageReq,
			@RequestParam String VIPService,
			@RequestParam(required = false) String VIPServiceDetail,
			@RequestParam(required = false) String oriented,
			@RequestParam String limitDate, @RequestParam String invoice,
			@RequestParam(required = false) String relatedMaterial,
			@RequestParam String remarks, HttpServletRequest request,
			HttpServletResponse response) {

		String clientId = (String) request.getSession().getAttribute(Constant.USER_ID);

		String path = null;
		String fileName = null;
		// System.out.println("file+"+file+"filename"+file.getOriginalFilename());//不上传文件还是会显示有值
		if (file.getSize() != 0)// 有上传文件的情况
		{
			path = UploadPath.getGoodsPath();// 不同的地方取不同的上传路径
			fileName = file.getOriginalFilename();
			fileName = clientId + "_" + fileName;// 文件名
			File targetFile = new File(path, fileName);
			try { // 保存 文件
				file.transferTo(targetFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// System.out.println("path+fileName+" + path + "-" + fileName);
		}
		// 没有上传文件的情况path 和 filenName默认为null

		// ////////////////////////////////////////////
		
		boolean flag = goodsInfoService.updateGoods(id, name, type, weight,
				transportType, transportReq, startPlace, endPlace, damageReq,
				VIPService, oriented, limitDate, invoice, remarks, clientId,
				path, fileName);
		if (flag == true) {
			try {
				response.sendRedirect("goodsform?flag=1");
			} catch (IOException e) {
				// 
				e.printStackTrace();
			}
		}
		return mv;
	}

	@RequestMapping("deletegoods")
	/**
	 * 删除用户
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView deleteGoods(@RequestParam String id,
			HttpServletRequest request, HttpServletResponse response) {

		boolean flag = goodsInfoService.deleteGoods(id);
		try {
			if (flag == true)
				response.sendRedirect("goodsform?flag=1");
			else
				System.out.println("删除失败");// 应记录日志
		} catch (IOException e) {
			// 
			// 此处应记录日志
			e.printStackTrace();

		}

		return mv;
	}
	
	@RequestMapping(value = "downloadgoodsrelated", method = RequestMethod.GET)
	/**
	 * 删除
	 */
	public ModelAndView downloadGoodsRelated(@RequestParam String id,// GET方式传入，在action中
			HttpServletRequest request, HttpServletResponse response) {
		GoodsClientView goodsformInfo = goodsInfoService.getAllGoodsDetail(id);
			String file = goodsformInfo.getRelatedMaterial();
			DownloadFile.downloadFile(file,request,response);
		return mv;

	}
	/**
	 * 我的信息=我的货物
	 * @Title: getUserCargoResource 
	 *  
	 * @param: @param session
	 * @param: @return 
	 * @return: String 
	 * @throws: 异常 
	 * @author: chendonghao 
	 * @date: 2015年7月3日 下午4:56:10
	 */
	@ResponseBody
	@RequestMapping(value="getUserCargoResourceAjax",produces = "text/html;charset=UTF-8")
	public String getUserCargoResource(HttpSession session,PageUtil pageUtil){
		JSONArray jsonArray=goodsInfoService.getUserCargoResource(session,pageUtil);
		return jsonArray.toString();
	}
	
	/**
	 * 我的信息-货物信息-总记录数
	 * @Title: getUserCargoResourceTotalRows 
	 *  
	 * @param: @param session
	 * @param: @return 
	 * @return: Integer 
	 * @throws: 异常 
	 * @author: chendonghao 
	 * @date: 2015年7月3日 下午4:58:56
	 */
	@ResponseBody
	@RequestMapping("getUserCargoResourceTotalRowsAjax")
	public Integer getUserCargoResourceTotalRows(HttpSession session){
		return goodsInfoService.getUserCargoTotalRows(session);
	}

}
