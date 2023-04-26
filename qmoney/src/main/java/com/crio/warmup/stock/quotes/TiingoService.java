
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {

  public static final String  Token="b92af9bbb3c785abfd85b9e8203b7f995790c9aa";
  private RestTemplate restTemplate;

  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)   throws JsonProcessingException {
    
      // RestTemplate restTemplate=new RestTemplate();
      //   TiingoCandle[] result=restTemplate.getForObject( buildUri(symbol, from, to),TiingoCandle[].class);
    //     String response=restTemplate.getForObject(buildUri(symbol, from, to), String.class);
                
  //          ObjectMapper obj=new ObjectMapper();
          //  obj.registerModule(new JavaTimeModule());
      //      TiingoCandle[] result=obj.readValue(response, TiingoCandle[].class);
            
         //   if(result==null)
        //     {
        //     return new ArrayList<>();
         //      }
   
         //      return Arrays.asList(result);
         String response=restTemplate.getForObject(buildUri(symbol, from, to), String.class);
             
         ObjectMapper obj=new ObjectMapper();
         obj.registerModule(new JavaTimeModule());
         TiingoCandle[] result=obj.readValue(response, TiingoCandle[].class);
        // System.out.println("hello iside-------");
         if(result==null)
          {
          return new ArrayList<>();
            }

            return Arrays.asList(result);

  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    
    String x=startDate.toString();

    String uriTemplate = "https://api.tiingo.com/tiingo/daily/"+symbol+"/prices?startDate="+startDate.toString()+"&endDate="+endDate.toString()+"&token=b92af9bbb3c785abfd85b9e8203b7f995790c9aa";
    
                return uriTemplate;
      }

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement getStockQuote method below that was also declared in the interface.

  // Note:
  // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
  // 2. Run the tests using command below and make sure it passes.
  //     


  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Write a method to create appropriate url to call the Tiingo API.

}
