package cn.edu.bjtu.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.catalina.ha.backend.Sender;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import cn.edu.bjtu.dao.SearchDao;

/**
 * ����dao��
 * @author RussWest0
 *
 */
@Repository
public class SearchDaoImpl implements SearchDao{

	@Resource 
	HibernateTemplate ht;
	@Override
	public List getLineResourceByStartPlace(String startPlace) {
		// TODO Auto-generated method stub
		return ht.find("from LineCarrierView where startPlace like '%"+startPlace+"%'");
	}

	@Override
	public List getLineResourceByEndPlace(String endPlace) {
		// TODO Auto-generated method stub
		return ht.find("from LineCarrierView where endPlace like '%"+endPlace+"%'");
	}

	@Override
	public List getCitylineResourceByName(String name) {
		// TODO Auto-generated method stub
		return ht.find("from CityCarrierView where name like '%"+name+"%'");
	}
	
	@Override
	public List getGoodsResourceByName(String name) {
		
		return ht.find("from GoodsClientView where name like '%"+name+"%'");
	}
	
	@Override
	public List getCompanyResourceByCompanyName(String companyName){
		
		return ht.find("from Carrierinfo where companyName like '%"+companyName+"%'");
	}
	
	@Override
	public List getCarResourceByCarNum(String carNum){
		return ht.find("from CarCarrierView where carNum like '%"+carNum+"%'");
	}
	
	@Override
	public List getWarehouseResourceByName(String name){
		return ht.find("from WarehouseCarrierView where name like '%"+name+"%'");
	}
}