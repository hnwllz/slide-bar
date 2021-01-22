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
	 * 获取图片，由于spring boot打包成jar之后，获取到获取不到resources里头的图片，对此进行处理
	 * @param path
	 * @return 
	 * @author pangxianhe
	 * @date 2020年1月2日
	 */
	public List<File> queryFileList(String path) {
 
		//获取容器资源解析器
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        List<File> filelist = new ArrayList<File>();
		// 获取远程服务器IP和端口
		try {
			//获取所有匹配的文件
            Resource[] resources = resolver.getResources(path);
            
            for(Resource resource : resources) {
                //获得文件流，因为在jar文件中，不能直接通过文件资源路径拿到文件，但是可以在jar包中拿到文件流
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
	 * 获取验证码
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/getImageVerifyCode", method = RequestMethod.POST)
	@ResponseBody
//	@RequestMapping("/getImageVerifyCode")
	public String getImageVerifyCode() throws Exception {
		
		// 读取图库目录
		List<File> imgList = queryFileList(IMG_PATH);
		int randNum = new Random().nextInt(imgList.size());
		File targetFile = imgList.get(randNum);
		List<File> tempimgList = queryFileList(TEMP_IMG_PATH);
		File tempImgFile = tempimgList.get(0);
		// 根据模板裁剪图片
		Map<String, Object> resultMap = VerifyImageUtil.pictureTemplatesCut(tempImgFile, targetFile);
		int xWidth = Integer.parseInt(resultMap.get("xWidth").toString());
		// sessionId 为key，value滑动距离X轴，缓存120秒
//		redisTemplate.(session.getId(), xWidth, IMG_CACHE_EX_TIME);
		
	   String chenckMoveid = IdWorker.getId();
	   repo.saveVerifyData(chenckMoveid, xWidth);
//	   BoundValueOperations<String, Object> redisOper = redisTemplate.boundValueOps(chenckMoveid);
//	   redisOper.set(xWidth, IMG_CACHE_EX_TIME, TimeUnit.SECONDS);
	   
	   
		// 移除map的滑动距离，不返回给前端
		resultMap.remove("xWidth");
		resultMap.put("chenckMoveid", chenckMoveid);
		return JSONObject.fromObject(Result.success().setData(JSONObject.fromObject(resultMap).toString())).toString();
	}
	
	@RequestMapping(value = "/verifydata", method = RequestMethod.POST)
	@ResponseBody
	public String verify(String x_index, String chenckMoveid){
		if (ObjectUtil.isNull(x_index)||ObjectUtil.isNull(chenckMoveid)) {
			return JSONObject.fromObject(Result.error("请使用滑块验证码")).toString();
		} else {
			Object x_index_orld = repo.getVerifyOffsetXByVerfiyId(String.valueOf(chenckMoveid));
			if (ObjectUtil.isNull(x_index_orld)) {
				return  JSONObject.fromObject(Result.error("请刷新滑块验证码")).toString();
			} else {
				 Double dMoveLength = Double.valueOf(x_index_orld.toString());
				 Double xWidth = Double.valueOf(x_index);
				 if (Math.abs(xWidth - dMoveLength) > 10) {
	                //验证不通过
					return  JSONObject.fromObject(Result.error("请拖动到正确的位置")).toString();
	            } else {
	               //验证通过
	            	repo.deleteVerifyData(String.valueOf(chenckMoveid));
	            	return JSONObject.fromObject(Result.success()).toString();
	            }
			}
		}
	}
}

