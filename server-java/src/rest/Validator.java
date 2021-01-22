package rest;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Validator {
	private static final String IMG_PATH = "/image/picture/*.*";
	private static final String TEMP_IMG_PATH = "/image/temp/temp.png";
	private static final Long IMG_CACHE_EX_TIME = 120L;
	private static VerifyDataRepository repo = new VerifyDataRepository();
	
	/**
	 * ��ȡͼƬ������spring boot�����jar֮�󣬻�ȡ����ȡ����resources��ͷ��ͼƬ���Դ˽��д���
	 * @param path
	 * @return 
	 * @author pangxianhe
	 * @date 2020��1��2��
	 */
	public List<File> queryFileList(String path) {
 
		//��ȡ������Դ������
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        List<File> filelist = new ArrayList<File>();
		// ��ȡԶ�̷�����IP�Ͷ˿�
		try {
			//��ȡ����ƥ����ļ�
            Resource[] resources = resolver.getResources(path);
            
            for(Resource resource : resources) {
                //����ļ�������Ϊ��jar�ļ��У�����ֱ��ͨ���ļ���Դ·���õ��ļ������ǿ�����jar�����õ��ļ���
                InputStream stream = resource.getInputStream();
                String targetFilePath =resource.getFilename();
                File ttfFile = new File(targetFilePath);
               /* if(!ttfFile.getParentFile().exists()) {
                	ttfFile.getParentFile().mkdir();
                }*/

                FileUtils.copyInputStreamToFile(stream, ttfFile);
                filelist.add(ttfFile);
            }
		}catch (Exception e) {
			e.printStackTrace();
		}	
		return filelist;
	}
 
/**
	 * ��ȡ��֤��
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/getImageVerifyCode", method = RequestMethod.POST)
	@ResponseBody
//	@RequestMapping("/getImageVerifyCode")
	public String getImageVerifyCode() throws Exception {
		
		// ��ȡͼ��Ŀ¼
		List<File> imgList = queryFileList(IMG_PATH);
		int randNum = new Random().nextInt(imgList.size());
		File targetFile = imgList.get(randNum);
		List<File> tempimgList = queryFileList(TEMP_IMG_PATH);
		File tempImgFile = tempimgList.get(0);
		// ����ģ��ü�ͼƬ
		Map<String, Object> resultMap = VerifyImageUtil.pictureTemplatesCut(tempImgFile, targetFile);
		int xWidth = Integer.parseInt(resultMap.get("xWidth").toString());
		// sessionId Ϊkey��value��������X�ᣬ����120��
//		redisTemplate.(session.getId(), xWidth, IMG_CACHE_EX_TIME);
		
	   String chenckMoveid = IdWorker.getId();
	   repo.saveVerifyData(chenckMoveid, xWidth);
//	   BoundValueOperations<String, Object> redisOper = redisTemplate.boundValueOps(chenckMoveid);
//	   redisOper.set(xWidth, IMG_CACHE_EX_TIME, TimeUnit.SECONDS);
	   
	   
		// �Ƴ�map�Ļ������룬�����ظ�ǰ��
		resultMap.remove("xWidth");
		resultMap.put("chenckMoveid", chenckMoveid);
		return JSONObject.fromObject(Result.success().setData(JSONObject.fromObject(resultMap).toString())).toString();
	}
	
	@RequestMapping(value = "/verifydata", method = RequestMethod.POST)
	@ResponseBody
	public String verify(String x_index, String chenckMoveid){
		if (ObjectUtil.isNull(x_index)||ObjectUtil.isNull(chenckMoveid)) {
			return JSONObject.fromObject(Result.error("��ʹ�û�����֤��")).toString();
		} else {
			Object x_index_orld = repo.getVerifyOffsetXByVerfiyId(String.valueOf(chenckMoveid));
			if (ObjectUtil.isNull(x_index_orld)) {
				return  JSONObject.fromObject(Result.error("��ˢ�»�����֤��")).toString();
			} else {
				 Double dMoveLength = Double.valueOf(x_index_orld.toString());
				 Double xWidth = Double.valueOf(x_index);
				 if (Math.abs(xWidth - dMoveLength) > 10) {
	                //��֤��ͨ��
					return  JSONObject.fromObject(Result.error("���϶�����ȷ��λ��")).toString();
	            } else {
	               //��֤ͨ��
	            	repo.deleteVerifyData(String.valueOf(chenckMoveid));
	            	return JSONObject.fromObject(Result.success()).toString();
	            }
			}
		}
	}
}

