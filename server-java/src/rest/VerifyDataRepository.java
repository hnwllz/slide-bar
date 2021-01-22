package rest;

import java.util.HashMap;
import java.util.Map;

public class VerifyDataRepository {
	public Double getVerifyOffsetXByVerfiyId(String verifyId){
		return StaticRepository.getVerifyOffsetXByVerfiyId(verifyId);
	}
	
	public void saveVerifyData(String verifyId, double offsetX){
		StaticRepository.saveVerifyData(verifyId, offsetX);
	}
	
	public static void deleteVerifyData(String verifyId){
		StaticRepository.deleteVerifyData(verifyId);
	}
}

class StaticRepository{
	private static Map<String, Double> dicRepository = new HashMap<String, Double>();
	
	public static Double getVerifyOffsetXByVerfiyId(String verifyId){
		if(dicRepository.containsKey(verifyId)){
			return dicRepository.get(verifyId);
		}
		
		return null;
	}
	
	public static void saveVerifyData(String verifyId, double offsetX){
		dicRepository.put(verifyId, offsetX);
	}
	
	public static void deleteVerifyData(String verifyId){
		if(dicRepository.containsKey(verifyId)){
			dicRepository.remove(verifyId);
		}
	}
}
