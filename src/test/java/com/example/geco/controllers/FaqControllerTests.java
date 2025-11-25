package com.example.geco.controllers;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.geco.AbstractControllerTest;
import com.example.geco.DataUtil;
import com.example.geco.domains.Faq;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FaqControllerTests extends AbstractControllerTest{
	@Nested
    class SuccessTests {
		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void canAddFaqByAdmin() throws Exception{
			mockAdminAuthentication(1, "admin@email.com");
			
			Faq faqA = DataUtil.createFaqA();
			String faqJson = objectMapper.writeValueAsString(faqA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/faq")
						.contentType(MediaType.APPLICATION_JSON)
						.content(faqJson)
			).andExpect(
					MockMvcResultMatchers.status().isCreated()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.faqId").exists()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.question").value(faqA.getQuestion())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.answer").value(faqA.getAnswer())
			);
		}
		
		@Test
		@WithMockUser(username = "staff@email.com", roles = "STAF")
		public void canGetFaq() throws Exception {
			mockStaffAuthentication(2, "staff@email.com");
			
			Faq faqA = DataUtil.createFaqA();
			faqService.addFaq(faqA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/faq/" + faqA.getFaqId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.faqId").value(faqA.getFaqId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.question").value(faqA.getQuestion())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.answer").value(faqA.getAnswer())
			);
		}
		
		@Test
		@WithMockUser(username = "staff@email.com", roles = "STAFF")
		public void canGetAllFaqs() throws Exception {
			mockStaffAuthentication(2, "staff@email.com");
			
			Faq faqA = DataUtil.createFaqA();
			faqService.addFaq(faqA);
			
			Faq faqB = DataUtil.createFaqB();
			faqService.addFaq(faqB);
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/faq")
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[0].faqId").value(faqA.getFaqId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[0].question").value(faqA.getQuestion())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[0].answer").value(faqA.getAnswer())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[1].faqId").value(faqB.getFaqId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[1].question").value(faqB.getQuestion())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[1].answer").value(faqB.getAnswer())
			);
		}
		
		@Test
		@WithMockUser(username = "user@email.com", roles = "USER")
		public void canGetActiveAllFaqs() throws Exception {
			mockUserAuthentication(3, "user@email.com");
			
			Faq faqA = DataUtil.createFaqA();
			faqA.setActive(false);
			faqRepository.save(faqA);
			
			Faq faqB = DataUtil.createFaqB();
			faqRepository.save(faqB);
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/faq/active")
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[0].faqId").value(faqB.getFaqId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[0].question").value(faqB.getQuestion())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[0].active").value(faqB.isActive())
			);
		}
		
		@Test
		@WithMockUser(username = "staff@email.com", roles = "STAFF")
		public void canGetInactiveAllFaqs() throws Exception {
			mockStaffAuthentication(2, "staff@email.com");
			
			Faq faqA = DataUtil.createFaqA();
			faqA.setActive(false);
			faqRepository.save(faqA);
			
			Faq faqB = DataUtil.createFaqB();
			faqRepository.save(faqB);
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/faq/inactive")
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[0].faqId").value(faqA.getFaqId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[0].question").value(faqA.getQuestion())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[0].active").value(faqA.isActive())
			);
		}
		
		@Test
		@WithMockUser(username = "staff@email.com", roles = "STAFF")
		public void canGetAllFaqsEmpty() throws Exception {
			mockStaffAuthentication(2, "staff@email.com");
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/faq")
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$").isEmpty()
			);
		}
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void canUpdateFaq() throws Exception {
			mockAdminAuthentication(2, "admin@email.com");
			
			Faq faqA = DataUtil.createFaqA();
			Faq savedFaqA = faqService.addFaq(faqA);
			
			faqA.setQuestion("New question for the said FAQ.");
			faqA.setAnswer("New answer for the said FAQ.");
			String faqJson = objectMapper.writeValueAsString(faqA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/faq/" + savedFaqA.getFaqId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(faqJson)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.faqId").value(savedFaqA.getFaqId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.question").value(faqA.getQuestion())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.answer").value(faqA.getAnswer())
			);
		}
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void canUpdateFaqQuestionOnly() throws Exception {
			mockAdminAuthentication(1, "admin@email.com");
			
			Faq faqA = DataUtil.createFaqA();
			Faq savedFaqA = faqService.addFaq(faqA);
			
			Faq newFaq = new Faq();
			newFaq.setQuestion("What is this park all about?");
			String faqJson = objectMapper.writeValueAsString(newFaq);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/faq/" + savedFaqA.getFaqId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(faqJson)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.faqId").value(savedFaqA.getFaqId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.question").value(newFaq.getQuestion())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.answer").value(savedFaqA.getAnswer())
			);
		}
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void canUpdateFaqAnswerOnly() throws Exception {
			mockAdminAuthentication(1, "admin@email.com");
			
			Faq faqA = DataUtil.createFaqA();
			Faq savedFaqA = faqService.addFaq(faqA);
			
			Faq newFaq = new Faq();
			newFaq.setAnswer("New answer for the said question.");
			String faqJson = objectMapper.writeValueAsString(newFaq);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/faq/" + savedFaqA.getFaqId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(faqJson)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.faqId").value(savedFaqA.getFaqId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.question").value(savedFaqA.getQuestion())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.answer").value(newFaq.getAnswer())
			);
		}
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void canReorderFaqList() throws Exception {
			mockAdminAuthentication(1, "admin@email.com");
			
			Faq faqA = DataUtil.createFaqA();
			Faq savedFaqA = faqService.addFaq(faqA);
		
			Faq faqB = DataUtil.createFaqB();
			Faq savedFaqB = faqService.addFaq(faqB);
			
			savedFaqA.setDisplayOrder(2);
			savedFaqB.setDisplayOrder(1);
			
			List<Faq> faqList = new ArrayList<>();
			faqList.add(savedFaqA);
			faqList.add(savedFaqB);

			String listJson = objectMapper.writeValueAsString(faqList);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/faq/reorder")
						.contentType(MediaType.APPLICATION_JSON)
						.content(listJson)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[0].faqId").value(savedFaqB.getFaqId())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[0].displayOrder").value(1)
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[1].faqId").value(savedFaqA.getFaqId())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[1].displayOrder").value(2)
			);
		}

		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void canSoftDeleteFaq() throws Exception {
			mockAdminAuthentication(1, "admin@email.com");
			
			Faq faqA = DataUtil.createFaqA();
			Faq savedFaqA = faqService.addFaq(faqA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.delete("/faq/" + savedFaqA.getFaqId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNoContent()
			);
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/faq/" + savedFaqA.getFaqId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.active").value("false")
			);
		}


		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void canHardDeleteFaq() throws Exception {
			mockAdminAuthentication(1, "admin@email.com");
			
			Faq faqA = DataUtil.createFaqA();
			Faq savedFaqA = faqService.addFaq(faqA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.delete("/faq/" + savedFaqA.getFaqId())
						.param("soft", "false")
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNoContent()
			);
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/faq/" + savedFaqA.getFaqId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			);
		}
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void canRestoreFaq() throws Exception {
			mockAdminAuthentication(1, "admin@email.com");
			
			Faq faqA = DataUtil.createFaqA();
			Faq savedFaqA = faqService.addFaq(faqA);
			faqService.softDeleteFaq(savedFaqA.getFaqId());
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/faq/admin/restore/" + savedFaqA.getFaqId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNoContent()
			);
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/faq/" + savedFaqA.getFaqId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.active").value("true")
			);
		}
	}
	
	@Nested
    class FailureTests {
		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void cannotAddFaqMissingQuestion() throws Exception {
			mockAdminAuthentication(1, "admin@email.com");
			
			Faq faqA = DataUtil.createFaqA();
			faqA.setQuestion(null);
			
			String faqJson = objectMapper.writeValueAsString(faqA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/faq")
						.contentType(MediaType.APPLICATION_JSON)
						.content(faqJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Question is missing.")
			);
		}
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void cannotAddFaqShortQuestion() throws Exception {
			mockAdminAuthentication(1, "admin@email.com");
			
			Faq faqA = DataUtil.createFaqA();
			faqA.setQuestion("what?");
			
			String faqJson = objectMapper.writeValueAsString(faqA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/faq")
						.contentType(MediaType.APPLICATION_JSON)
						.content(faqJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Question must have at least 10 characters.")
			);
		}

		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void cannotAddFaqMissingAnswer() throws Exception {
			mockAdminAuthentication(1, "admin@email.com");
			
			Faq faqA = DataUtil.createFaqA();
			faqA.setAnswer(null);
			
			String faqJson = objectMapper.writeValueAsString(faqA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/faq")
						.contentType(MediaType.APPLICATION_JSON)
						.content(faqJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Answer is missing.")
			);
		}
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void cannotAddFaqShortAnswer() throws Exception {
			mockAdminAuthentication(1, "admin@email.com");
			
			Faq faqA = DataUtil.createFaqA();
			faqA.setAnswer("what?");
			
			String faqJson = objectMapper.writeValueAsString(faqA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/faq")
						.contentType(MediaType.APPLICATION_JSON)
						.content(faqJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Answer must have at least 10 characters.")
			);
		}
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void cannotAddFaqQuestionAlreadyExist() throws Exception {
			mockAdminAuthentication(1, "admin@email.com");
			
			Faq faqA = DataUtil.createFaqA();
			faqRepository.save(faqA);
			
			String faqJson = objectMapper.writeValueAsString(faqA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/faq")
						.contentType(MediaType.APPLICATION_JSON)
						.content(faqJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Question \"" + faqA.getQuestion() + "\" already exists.")
			);
		}
		
		@Test
		@WithMockUser(username = "staff@email.com", roles = "STAFF")
		public void cannotGetFaq() throws Exception {
			mockStaffAuthentication(2, "staff@email.com");
			
			int id = 0;
			mockMvc.perform(
					MockMvcRequestBuilders.get("/faq/" + id)
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("FAQ with ID '" + id + "' not found.")
			);
		}
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void cannotUpdateFaqMissingQuestionAndAnswer() throws Exception {
			mockAdminAuthentication(1, "admin@email.com");
			
			Faq faqA = DataUtil.createFaqA();
			Faq savedFaqA = faqService.addFaq(faqA);;

			Faq newFaq = new Faq();
			newFaq.setQuestion(null);
			newFaq.setAnswer(null);
			String faqJson = objectMapper.writeValueAsString(newFaq);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/faq/" + savedFaqA.getFaqId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(faqJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("FAQ question and answer is empty.")
			);
		}
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void cannotUpdateFaqMissing() throws Exception {
			mockAdminAuthentication(1, "admin@email.com");
			
			Faq faqA = DataUtil.createFaqA();
			faqA.setFaqId(0);
			String faqJson = objectMapper.writeValueAsString(faqA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/faq/" + faqA.getFaqId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(faqJson)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("FAQ with ID \"" + faqA.getFaqId() + "\" not found.")
			);
		}
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void cannotUpdateFaqQuestionAlreadyExist() throws Exception {
			mockAdminAuthentication(1, "admin@email.com");
			
			Faq faqA = DataUtil.createFaqA();
			Faq savedFaqA = faqService.addFaq(faqA);
			
			Faq faqB = DataUtil.createFaqB();
			Faq savedFaqB = faqService.addFaq(faqB);
			
			faqA.setQuestion(savedFaqB.getQuestion());
			String faqJson = objectMapper.writeValueAsString(faqA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/faq/" + savedFaqA.getFaqId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(faqJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Another FAQ with the same question already exists.")
			);
		}
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void cannotDeleteFaq() throws Exception {
			mockAdminAuthentication(1, "admin@email.com");
			
			int id = 0;
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/faq/" + id)
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("FAQ with ID '" + id + "' not found.")
			);
		}
	} 
}
