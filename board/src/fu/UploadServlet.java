package fu;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet(
		urlPatterns="/upload", 
		initParams= {
			@WebInitParam(
				name="uploadPath", 
				value="C:\\uploadfile\\upload\\"
			)
		}
	)
@MultipartConfig(
			location = "C:\\uploadfile\\upload\\",
			maxFileSize = 1024L,
			maxRequestSize = -1,
			fileSizeThreshold = -1
		)

public class UploadServlet extends HttpServlet {
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		resp.setContentType("text/html; charset=utf-8");
		
		PrintWriter writer = resp.getWriter();
		writer.println("<html><body>");
		
		String contentType = req.getContentType();
		if(contentType != null && contentType.toLowerCase().startsWith("multipart/")) {
			printPartInfo(req,writer);
		} else {
			writer.println("multipart가 아님(일반 파라미터)");
		}
		writer.println("</body></html>");
	}
	
	private void printPartInfo(HttpServletRequest req, PrintWriter writer) throws ServletException, IOException {
		Collection<Part> parts = req.getParts();
		for(Part part : parts) {
			writer.println("<br> name=" + part.getName());
			String contentType = part.getContentType();
			writer.println("<br> contentType = " + contentType);
			if(contentType == null) {
				part.delete();
			} else if(contentType.startsWith("application/")) {
				long size = part.getSize();
				writer.println("<br> size = " + size);
				String filename = getFileName(part);
				writer.println("<br> filename = " + filename);
				if(size > 0) {
					part.write("c:\\uploadfile\\"+filename);
					part.delete();
				}
			} else {
				String value = readParameterValue(part);
				writer.println("<hr/>");
			}
			writer.println("<hr/>");
		}
	}
	
	private String getFileName(Part part) throws UnsupportedEncodingException{
		for (String cd : part.getHeader("Content-Disposition").split(";")) {
			if(cd.trim().startsWith("filename")) {
				String tmp = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
				tmp = tmp.substring(tmp.indexOf(":")+1);
					return tmp;
			}
		}
		return null;
	}
	
	private String readParameterValue(Part part) throws IOException{
		InputStreamReader isr = new InputStreamReader(part.getInputStream(),"utf-8");
		char[] data = new char[512];
		int len = -1;
		StringBuilder builder = new StringBuilder();
		while((len = isr.read(data)) != -1) {
			builder.append(data, 0 , len);
		}
		return builder.toString();
	}
}
