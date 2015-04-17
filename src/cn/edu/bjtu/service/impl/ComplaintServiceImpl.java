package cn.edu.bjtu.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import cn.edu.bjtu.dao.BaseDao;
import cn.edu.bjtu.dao.ComplaintDao;
import cn.edu.bjtu.service.ComplaintService;
import cn.edu.bjtu.service.OrderService;
import cn.edu.bjtu.util.IdCreator;
import cn.edu.bjtu.vo.Complaintform;

@Service("complaintServiceImpl")
public class ComplaintServiceImpl implements ComplaintService{

	@Resource
	ComplaintDao complaintDao;
	@Resource 
	Complaintform complaintform;
	@Resource 
	OrderService orderService;
	@Resource
	BaseDao baseDao;
	
	@Override
	public List getUserCompliant(String userId) {
		// TODO Auto-generated method stub
		return complaintDao.getUserCompliant(userId);
	}

	@Override
	public Complaintform getComplaintInfo(String id) {
		
		return complaintDao.getComplaintInfo(id);
	}
	
	@Override
	/**
	 * ����Ͷ��
	 */
	public boolean insertComplaint(String type, String theme,
			String content, String orderNum, String carrierId,
			HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub

		complaintform.setId(IdCreator.createCityLineId());
		complaintform.setType(type);
		complaintform.setTheme(theme);
		complaintform.setContent(content);

		complaintform.setCarrierId(carrierId);
		complaintform.setClientId(carrierId);
		complaintform.setRelDate(new Date());
		if(orderNum!=null)//�����Ƕ����ų��������
		{
			if(orderService.getOrderIdByOrderNum(orderNum).isEmpty())
			{
				System.out.println("�Ҳ����������");
				/*try {
					response.sendRedirect("mycomplaint");//�ض�����ʾ���½��
					return;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				return false;
			}
			else {
				complaintform.setOrderId(orderService.
						getOrderIdByOrderNum(orderNum).
						get(0).toString());
			}
		}
		return baseDao.save(complaintform);
	
	}
}