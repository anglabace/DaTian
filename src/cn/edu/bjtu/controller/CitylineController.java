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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import cn.edu.bjtu.bean.search.CityLineSearchBean;
import cn.edu.bjtu.service.CitylineService;
import cn.edu.bjtu.service.CommentService;
import cn.edu.bjtu.service.CompanyService;
import cn.edu.bjtu.service.FocusService;
import cn.edu.bjtu.util.Constant;
import cn.edu.bjtu.util.DownloadFile;
import cn.edu.bjtu.util.PageUtil;
import cn.edu.bjtu.util.UploadPath;
import cn.edu.bjtu.vo.Carrierinfo;
import cn.edu.bjtu.vo.Cityline;
import cn.edu.bjtu.vo.Comment;
import cn.edu.bjtu.vo.Linetransport;

import com.alibaba.fastjson.JSONArray;

@Controller
/**
 * 城市配送相关
 * @author RussWest0
 *
 */
public class CitylineController {
	@Autowired
	CommentService commentService;
	@Resource
	CitylineService citylineService;
	@Resource
	CompanyService companyService;
	@Autowired
	FocusService focusService;
	ModelAndView mv = new ModelAndView();
	
	/**
	 * 资源栏所有城市配送线路信息
	 * @param flag
	 * @return
	 */
	@RequestMapping(value="/cityline",params="flag=0")
	public String getAllCityline() {
		
		return "resource_list2";
		
	}
	/**
	 * 资源栏城市配送筛选
	 * @param citylineBean
	 * @param page
	 * @param session
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value="getCityLineAjax",produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getCitylineAjax(CityLineSearchBean citylineBean,
			PageUtil page, HttpSession session, HttpServletResponse response,
			Model model) {
		JSONArray linetransportArray = citylineService.getSelectedLineNew(
				citylineBean,page,session);
		//model.addAttribute("count", 100);
		return linetransportArray.toString();
	}
	
	/**
	 * 获取城市配送筛选总条数
	 * @param lineBean
	 * @return
	 */
	@RequestMapping("getSelectedCityLineTotalRowsAjax")
	@ResponseBody
	public Integer getSelectedLineTotalRows(CityLineSearchBean citylineBean){
		Integer count=citylineService.getSelectedCityLineTotalRows(citylineBean);
		return count;
	}
	
	/**
	 * 我的信息-用户的所有城市配送线路信息
	 * @param flag
	 * @return
	 */
	@RequestMapping(value="/cityline",params="flag=1")
	public String getUserCityline(@RequestParam int flag,
			HttpServletRequest request) {
		return "mgmt_r_city";// 点击左边城市配送显示所有信息
	}

	@RequestMapping(value = "/citylinedetail", method = RequestMethod.GET)
	/**
	 * 获取城市配送线路详情
	 * @param citylineId
	 * @param carrierId
	 * @param flag
	 * @return
	 */
	public ModelAndView getCitylineInfo(
			@RequestParam("citylineId") String citylineId,
			@RequestParam("carrierId") String carrierId,
			@RequestParam("flag") int flag,
			HttpServletRequest request) {
		Cityline citylineInfo = citylineService.getCitylineInfo(citylineId); // 需要重构,返回一条具体的线路不是list
		String clientId = (String) request.getSession().getAttribute(Constant.USER_ID);
		List focusList = focusService.getFocusList(clientId,"cityline");
		mv.addObject("focusList", focusList);
		mv.addObject("citylineInfo", citylineInfo);
		if (flag == 0) {
			Carrierinfo carrierInfo = companyService.getCompanyById(carrierId);
			List<Comment> commentList=commentService.getCompanyComment(carrierId);
			mv.addObject("commentList",commentList);
			mv.addObject("carrierInfo", carrierInfo);
			//需要获取资源对应的公司的评价平均数bean
			Comment comment=commentService.getCompanyAverageCommentRate(carrierId);
			mv.addObject("avgComment", comment);
			mv.setViewName("resource_detail2");// 资源栏点击详情的页面
		} else if (flag == 1)
			mv.setViewName("mgmt_r_city");// 3是有更新和删除操作的页面
		else if (flag == 2)
			mv.setViewName("mgmt_r_city4");// 4是点击详情的页面
		else if (flag == 3)
			mv.setViewName("mgmt_r_city3");// 点击更新 的页面
		return mv;
	}


	/**
	 * 新增城市配送线路
	 */
	@RequestMapping(value = "/insertCityLine", method = RequestMethod.POST)
	public String insertNewCityline(Cityline cityline,MultipartFile file,
			HttpServletRequest request) {
		boolean flag=citylineService.insertNewCityline(cityline,request,file);
		return "redirect:cityline?flag=1";
	}
	
	@RequestMapping(value = "/updateCityline", method = RequestMethod.POST)
	public String updateNewCityline(Cityline cityline,MultipartFile file,
			HttpServletRequest request) {
		boolean flag=citylineService.updateNewCityline(cityline,request,file);
		return "redirect:cityline?flag=1";
	}
	
	//public 

	/**
	 * 删除
	 */
	@RequestMapping(value = "citydelete", method = RequestMethod.GET)
	public String deleteCityline(@RequestParam String id) {
		citylineService.deleteCityline(id);
			return "redirect:cityline?flag=1";

	}

	@RequestMapping(value = "downloaddetailprice", method = RequestMethod.GET)
	/**
	 * 删除
	 */
	public ModelAndView downloadDetailPrice(@RequestParam String id,// GET方式传入，在action中
			HttpServletRequest request, HttpServletResponse response) {
		Cityline citylineInfo = citylineService.getCitylineInfo(id); // 需要重构,返回一条具体的线路不是list
			String file = citylineInfo.getDetailPrice();
			DownloadFile.downloadFile(file,request,response);
		return mv;

	}
	
	/**
	 * 我的信息-城市配送
	 * @Title: getUserCitylineResource 
	 *  
	 * @param: @param session
	 * @param: @return 
	 * @return: String 
	 * @throws: 异常 
	 * @author: chendonghao 
	 * @date: 2015年7月3日 上午9:53:18
	 */
	@RequestMapping(value="getUserCitylineResourceAjax",produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getUserCitylineResource(HttpSession session,PageUtil pageUtil){
		JSONArray jsonArray=citylineService.getUserCitylineResource(session,pageUtil);
		
		return jsonArray.toString();
	}
	
	/**
	 * 我的信息-城市配送-总记录数
	 * @Title: getUserCitylineResourceTotalRows 
	 *  
	 * @param: @param session
	 * @param: @return 
	 * @return: Integer 
	 * @throws: 异常 
	 * @author: chendonghao 
	 * @date: 2015年7月3日 上午9:55:10
	 */
	@RequestMapping("getUserCitylineResourceTotalRows")
	@ResponseBody
	public Integer getUserCitylineResourceTotalRows(HttpSession session){
		
		return citylineService.getUserCitylineResourceTotalRows(session);
	}
	
	/**
	 * 从公司详情页面进入订单加载城市配送资源 
	 * @param carrierId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="getCompanyCitylineAjax",produces = "text/html;charset=UTF-8")
	public String getCompanyCitylineAjax(String carrierId){
		
		return citylineService.getCompanyCitylineResource(carrierId);
	}

}
