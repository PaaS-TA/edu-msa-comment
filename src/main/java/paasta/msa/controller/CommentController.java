package paasta.msa.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import paasta.msa.common.CommonUtil;
import paasta.msa.service.CommentService;

/**
 * @author JaemooSong
 *
 */
@RestController
public class CommentController {

	@Resource(name = "commentService")
	private CommentService commentService;

	@RequestMapping(value = "/comments/{boardSeq}", method = RequestMethod.GET)
	public Map<String, Object> getComments(@PathVariable("boardSeq") Integer boardSeq) {
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> resultData = new HashMap<String, Object>();

		Map<String, Object> commentCount = null;
		List<Object> commentList = null;

		// parameter Setting
		try {
			paramMap.put("boardSeq", boardSeq);
		} catch (Exception e) {
			result.put("result", "ERROR");
			result.put("errMsg", "input parameter error.");
			e.printStackTrace();
			return result;
		}

		// Select CommentList
		try {

			// select CommentCount
			commentCount = commentService.getCommentCount(paramMap);
			long count = 0;
			
			if(commentCount != null) {
				count = (Long)commentCount.get("COUNT");
				resultData.put("commentCount", count);
			}

			// select CommentList
			if(count != 0) {
				commentList = (List<Object>)commentService.getCommentList(paramMap);
				resultData.put("commentList", commentList);
			}

			result.put("result", "SUCCESS");
			result.put("resultData", resultData);
		} catch (Exception e) {
			result.put("result", "ERROR");
			result.put("errMsg", e.getMessage());
			e.printStackTrace();
		}
		
		return result;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/comments", method = RequestMethod.POST)
	public Map<String, Object> postComment(HttpEntity<String> httpEntity) {
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = httpEntity.getBody();

		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> resultData = new HashMap<String, Object>();

		// parameter Setting
		try {
			if(jsonString ==  null || "".equals(jsonString)) {
				jsonString = "{}";
			}
			Map<String, String> jsonMap = mapper.readValue(jsonString, Map.class);

			Integer boardSeq = null;
			String comment = jsonMap.get("comment");
			String writeUserId = jsonMap.get("writeUserId");
			String writeUserName = jsonMap.get("writeUserName");
			
			try {
				boardSeq = Integer.parseInt(jsonMap.get("boardSeq"));
			} catch (Exception e) {
				throw new Exception("????????? ????????? ?????????????????????.");
			}
			
			// null String check
			if(CommonUtil.isEmptyString(comment)) {
				throw new Exception("????????? ????????? ?????? ??????????????????.");
			} else if(CommonUtil.isEmptyString(writeUserId)) {
				throw new Exception("????????? ID??? ?????? ??????????????????.");
			} else if(CommonUtil.isEmptyString(writeUserName)) {
				throw new Exception("????????? ?????? ?????? ??????????????????.");
			}

			paramMap.put("boardSeq", boardSeq);
			paramMap.put("comment", comment);
			paramMap.put("writeUserId", writeUserId);
			paramMap.put("writeUserName", writeUserName);
		} catch (Exception e) {
			result.put("result", "ERROR");
			result.put("errMsg", e.getMessage());
			e.printStackTrace();

			return result;
		}
		
		try {
			
			int insertCount = commentService.postComment(paramMap);
			if(insertCount != 1) {
				throw new Exception("????????? ????????? ?????????????????????.");
			}
			
			resultData.put("commentSeq", paramMap.get("commentSeq"));

			result.put("result", "SUCCESS");
			result.put("resultData", resultData);
		} catch (Exception e) {
			result.put("result", "ERROR");
			result.put("errMsg", e.getMessage());
			e.printStackTrace();
		}
		
		return result;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/comments/{commentSeq}", method = RequestMethod.PUT)
	public Map<String, Object> putComment(@PathVariable("commentSeq") int commentSeq, HttpEntity<String> httpEntity) {
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = httpEntity.getBody();

		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> resultData = new HashMap<String, Object>();
		Map<String, Object> commentCount = null;
		List<Object> commentList = null;
		
		// parameter Setting
		try {
			if(jsonString ==  null || "".equals(jsonString)) {
				jsonString = "{}";
			}
			Map<String, String> jsonMap = mapper.readValue(jsonString, Map.class);

			String comment = jsonMap.get("comment");
			String writeUserId = jsonMap.get("writeUserId");
			String boardSeq = jsonMap.get("boardSeq");
			
			// ?????? ?????? Data??? ?????? ??????
			if(CommonUtil.isEmptyString(comment)) {
				throw new Exception("????????? ?????? ?????? ????????????.");
			}
			
			paramMap.put("commentSeq", commentSeq);
			paramMap.put("comment", comment);
			paramMap.put("writeUserId", writeUserId);
			paramMap.put("boardSeq", boardSeq);
		} catch (Exception e) {
			result.put("result", "ERROR");
			result.put("errMsg", e.getMessage());
			e.printStackTrace();

			return result;
		}
		
		try {
			int updateCount = commentService.putComment(paramMap);
			if(updateCount != 1) {
				throw new Exception("????????? ????????? ?????????????????????.");
			}


			// select CommentCount
			commentCount = commentService.getCommentCount(paramMap);
			long count = 0;
			
			if(commentCount != null) {
				count = (Long)commentCount.get("COUNT");
				resultData.put("commentCount", count);
			}

			// select CommentList
			if(count != 0) {
				commentList = (List<Object>)commentService.getCommentList(paramMap);
				resultData.put("commentList", commentList);
			}
			
			result.put("result", "SUCCESS");
			result.put("resultData", resultData);
			
		} catch (Exception e) {
			result.put("result", "ERROR");
			result.put("errMsg", e.getMessage());
			e.printStackTrace();
		}
		
		return result;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/comments/{commentSeq}", method = RequestMethod.DELETE)
	public Map<String, Object> deleteComment(@PathVariable("commentSeq") int commentSeq) {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> resultData = new HashMap<String, Object>();
		
		// parameter Setting
		paramMap.put("commentSeq", commentSeq);
		// parameter Setting
		try {
			
			paramMap.put("commentSeq", commentSeq);
		} catch (Exception e) {
			result.put("result", "ERROR");
			result.put("errMsg", "????????? ????????? ?????????????????????.");
			e.printStackTrace();

			return result;
		}

		try {
			int deleteCount = commentService.deleteComment(paramMap);
			if(deleteCount == 0) {
				throw new Exception("????????? ????????? ?????????????????????.");
			}
			
			result.put("result", "SUCCESS");
			result.put("resultData", resultData);
		} catch (Exception e) {
			result.put("result", "ERROR");
			result.put("errMsg", e.getMessage());
			e.printStackTrace();
		}
		
		return result;
	}
}
