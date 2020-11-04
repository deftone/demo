package de.deftone.demo;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//spezieller junit4 Runner:
@RunWith(SpringRunner.class)
// nicht der ganze servlet container, nur eine mock Umgebung mit nur dem MVC teil:
@WebMvcTest
class DemoApplicationTests {


	@Autowired
	private MockMvc mockMvc;

	@Test
	void xxx() throws Exception {
		this.mockMvc.perform(get("/demo"))
				.andExpect(status().isOk());
	}

}
