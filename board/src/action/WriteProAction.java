package action;

import java.io.File;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import model.BoardDao;
import model.BoardDto;

public class WriteProAction implements CommandAction{

	@Override
	public String requestPro(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		request.setCharacterEncoding("UTF-8");
		String enctype="UTF-8";
		String path="d:\\javastudy\\jspupload";
		int size = 1024*1024;
		
		MultipartRequest mr = new MultipartRequest(request,path,size,enctype,new DefaultFileRenamePolicy());
		
		BoardDto article = new BoardDto();	//데이터를 처리할 빈
		article.setNum(Integer.parseInt(mr.getParameter("num")));
		article.setWriter(mr.getParameter("writer"));
		article.setEmail(mr.getParameter("email"));
		article.setSubject(mr.getParameter("subject"));
		article.setPass(mr.getParameter("pass"));
		article.setRegdate(new Timestamp(System.currentTimeMillis()));
		article.setRef(Integer.parseInt(mr.getParameter("ref")));
		article.setStep(Integer.parseInt(mr.getParameter("step")));
		article.setDepth(Integer.parseInt(mr.getParameter("depth")));
		article.setContent(mr.getParameter("content"));
		article.setIp(request.getRemoteAddr());
		
		String filename = mr.getOriginalFileName("file");
		
		
			if(filename == null) {
				article.setFilename("");
				article.setFilesize(0);
			} else {
				File file = new File(path + filename);
				System.out.println("파일 사이즈 : "+(int)file.length());
				
				if ((int)file.length() > size) {
					System.out.println("용량이 너무 큽니다.");
				} else {
				article.setFilename(filename);
				article.setFilesize((int)file.length());
				}
			}
		BoardDao dbPro = BoardDao.getInstance();	//DB연결
		dbPro.insertArticle(article);
		return "/board/writePro.jsp";				//해당 뷰 경로 반환
	}
}
