package org.r9.workspace.ui.dialog.chatgpt;

import java.util.Arrays;

import com.pi.code.tool.codeplayer.Workbench;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message; 

public class ChatGPTTest {
	  public static void main(String[] args) {
	        OpenAiClient openAiClient = OpenAiClient.builder()
	                .apiKey(Arrays.asList(  Workbench.CHATGPT_KEY ))
	                //鑷畾涔塳ey鐨勮幏鍙栫瓥鐣ワ細榛樿KeyRandomStrategy
	                //.keyStrategy(new KeyRandomStrategy())
	               // .keyStrategy(new KeyStrategyFunction())
	                //鑷繁鍋氫簡浠ｇ悊灏变紶浠ｇ悊鍦板潃锛屾病鏈夊彲涓嶄笉浼�
//	                .apiHost("https://鑷繁浠ｇ悊鐨勬湇鍔″櫒鍦板潃/")
	                .build();
	                //鑱婂ぉ妯″瀷锛歡pt-3.5
	        Message message1 = Message.builder().role(Message.Role.SYSTEM).content("You are a helpful assistant.").build();
	        Message message2 = Message.builder().role(Message.Role.USER).content("Who won the world series in 2020?").build();
	        Message message3 = Message.builder().role(Message.Role.ASSISTANT).content("The Los Angeles Dodgers won the World Series in 2020.").build();
	        Message message4 = Message.builder().role(Message.Role.USER).content("Where was it played?").build();
	    	 	       ChatCompletion chatCompletion = ChatCompletion.builder().messages(Arrays.asList(message1,message2 )).build();
	        ChatCompletionResponse chatCompletionResponse = openAiClient.chatCompletion(chatCompletion);
	        chatCompletionResponse.getChoices().forEach(e -> {
	            System.out.println(e.getMessage());
	        });
	    }
}
