package org.r9.workspace.ui.dialog.chatgpt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
 
import org.apache.commons.lang3.tuple.Pair; 

import com.pi.code.tool.codeplayer.Workbench;
import com.pi.code.tool.util.StringUtils;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message; 

public class SuggestionHelper {
	  public static interface FetchSuggestion{
		  void onResult(List<String> data);
	  }
	
	  public static void main(String[] args) {
		  String code = "public int rob(int[] nums) {\r\n" + 
		  		"        if (nums == null || nums.length == 0) {\r\n" + 
		  		"            return 0;\r\n" + 
		  		"        }\r\n" + 
		  		"        int length = nums.length;\r\n" + 
		  		"        if (length == 1) {\r\n" + 
		  		"            return XXXXX;\r\n" + 
		  		"        }\r\n" + 
		  		"        int[] dp = new int[length];\r\n" + 
		  		"        dp[0] = nums[0];\r\n" + 
		  		"        dp[1] = Math.max(nums[0], nums[1]);\r\n" + 
		  		"        for (int i = 2; i < length; i++) {\r\n" + 
		  		"            dp[i] = Math.max(dp[i - 2] + nums[i], dp[i - 1]);\r\n" + 
		  		"        }\r\n" + 
		  		"        return dp[length - 1];\r\n" + 
		  		"    }";
		  String correct = "nums[0]";
		   
		  FetchSuggestion callback = new FetchSuggestion() {

			@Override
			public void onResult(List<String> data) { 
				System.out.println( data );
			} 
		  };
		  
		  getSuggestions(code, correct, callback);
	  }
	  
	  public static void getSuggestions(String code, String correct, FetchSuggestion callback) {
	        OpenAiClient openAiClient = OpenAiClient.builder()
	                .apiKey(Arrays.asList(  Workbench.CHATGPT_KEY )) 
	                .build(); 
	        String input = "here is some java code, the missing part is \"" + correct + "\",represented by XXXXX,"
	        		+ "please provide 5 options for missing part. all options should be incorrect and misleading. \n\n"
	        		+ code;
	        Message message1 = Message.builder().role(Message.Role.SYSTEM).content("you are a quiz designer.").build();
	        Message message2 = Message.builder().role(Message.Role.USER).content(input).build();
	     //   Message message3 = Message.builder().role(Message.Role.ASSISTANT).content("The Los Angeles Dodgers won the World Series in 2020.").build();
	    //    Message message4 = Message.builder().role(Message.Role.USER).content("Where was it played?").build();
	    	 	       ChatCompletion chatCompletion = ChatCompletion.builder().messages(Arrays.asList(message1,message2 )).build();
	        ChatCompletionResponse chatCompletionResponse = openAiClient.chatCompletion(chatCompletion);
	        chatCompletionResponse.getChoices().forEach(e -> {
	            System.out.println(e.getMessage().getContent());
	            callback.onResult( parseData( e.getMessage().getContent()));
	        });
	    }
	  
	  public static List<String> parseData(String c) {
		  String[] lines = c.split("\n");
			
			Pair<String,List<String>> topics =  StringUtils.parseAsNumberedParagrah(lines,2);
			List<String> r= new ArrayList<>();
			if( topics != null ) {
				String title = topics.getLeft();
				if( !StringUtils.isEmpty(title)) {
				 
					for(String aline : topics.getRight()) {
						if( aline.length() == 0 ) continue;
						r.add(StringUtils.stripLeadingNumberOrder( aline) );
					}
				} else {
					for(String aline : topics.getRight()) {
						if( aline.length() == 0 ) continue;
						r.add(StringUtils.stripLeadingNumberOrder( aline) );
					}
				}
			} else {
				for(String line : lines) {
					if( line.trim().length() == 0) continue;
					r.add( line);
				} 
			} 
			return r;
	  }
	  
}
