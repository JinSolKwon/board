package action;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.BoardDao;
import model.BoardDto;

public class DownloadProAction implements CommandAction {
	
	@Override
	public String requestPro(HttpServletRequest req, HttpServletResponse resp) throws Throwable {
		req.setCharacterEncoding("UTF-8");
		
		int num = Integer.parseInt(req.getParameter("num"));
		
		BoardDto article = BoardDao.getInstance().getArticle(num);
		
		String filename = article.getFilename();
		String uploadFileName = "d:\\javastudy\\jspupload\\"+filename;
		
		System.out.println(filename);
		
		File downFile = new File(uploadFileName);
		
		if (downFile.exists() && downFile.isFile()) {
			filename = URLEncoder.encode(filename, "utf-8").replaceAll("\\+","%20");
			try {
				long filesize = downFile.length();
				
				resp.setContentType("application/octet-stream; charset=utf-8");
				resp.setContentLength((int) filesize);
				String strClient = req.getHeader("user-agent");
				
				if (strClient.indexOf("MSIE 5.5") != -1) {
					resp.setHeader("Content-Disposition", "filename="
                            + filename + ";");
                } else {
                	resp.setHeader("Content-Disposition",
                            "attachment; filename=" + filename + ";");
                }
				resp.setHeader("Content-Length", String.valueOf(filesize));
				resp.setHeader("Content-Transfer-Encoding", "binary;");
				resp.setHeader("Pragma", "no-cache");
				resp.setHeader("Cache-Control", "private");
 
                byte b[] = new byte[1024];
 
                BufferedInputStream in = new BufferedInputStream(
                        new FileInputStream(downFile));
 
                BufferedOutputStream out = new BufferedOutputStream(
                		resp.getOutputStream());
 
                int read = 0;
 
                while ((read = in.read(b)) != -1) {
                    out.write(b, 0, read);
                }
                out.flush();
                out.close();
                in.close();
				
			} catch (Exception e) {
				System.out.println("Download Exception : " + e.getMessage());
			}
		} else {
			System.out.println("Download Error : downFile Error [" + downFile + "]");
		}
		
		return null;
	}
}
