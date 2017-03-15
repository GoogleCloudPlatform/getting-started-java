package com.example.std.gettingstarted;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class GAETest  {

    @Value("${itb.auth.header.key}")
    public String ITB_AUTH_HEADER_KEY;

    @Autowired
    protected WebApplicationContext wac;

    protected MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        beforeSetup();
    }

    protected void beforeSetup(){

    }

    @After
    public void teardownHook() {
        beforeTeardown();
    }

    protected void beforeTeardown(){

    }




    public   String firePOST( String URL, String jsonBody) throws Exception {
        MvcResult result = mockMvc.perform(post(URL).content(jsonBody).contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        return result.getResponse().getContentAsString();
    }

    public   String firePOSTWithToken( String URL, String jsonBody, String token) throws Exception {
        MvcResult result = mockMvc.perform(post(URL).content(jsonBody).contentType(MediaType.APPLICATION_JSON)
                        .header(ITB_AUTH_HEADER_KEY,token)
        )
//                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        return result.getResponse().getContentAsString();
    }

    public  <T>T toClass(String JSON, Class clazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return  (T)mapper.readValue(JSON, clazz);
    }

    public  String fireGET( String url) throws Exception {
        MvcResult result = mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        return result.getResponse().getContentAsString();

    }

    public  String fireTextHtmlGET( String url) throws Exception {
        MvcResult result = mockMvc.perform(get(url).contentType(MediaType.TEXT_HTML))
//                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        return result.getResponse().getContentAsString();

    }

    public  String fireGETWithHeader( String url, String token) throws Exception {
        MvcResult result = mockMvc.perform(get(url).
                        contentType(MediaType.APPLICATION_JSON).header(ITB_AUTH_HEADER_KEY,token)
        )
//                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        return result.getResponse().getContentAsString();

    }

    public  String fireGETExpectingError( String url) throws Exception {
        MvcResult result = mockMvc.perform(get(url).
                        contentType(MediaType.APPLICATION_JSON)
        )
//                .andDo(print())

                .andExpect(status().is4xxClientError())
                .andReturn();
        return result.getResponse().getContentAsString();

    }

    public  String fireGETWithTokenExpectingError( String url, String token ) throws Exception {
        MvcResult result = mockMvc.perform(get(url).
                        contentType(MediaType.APPLICATION_JSON)
                        .header(ITB_AUTH_HEADER_KEY,token)
        )
//                .andDo(print())

                .andExpect(status().is4xxClientError())
                .andReturn();
        return result.getResponse().getContentAsString();

    }



}
