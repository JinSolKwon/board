package model;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BoardDao {
	private static BoardDao instance = null;
	private BoardDao() {}
	public static BoardDao getInstance() {
		if(instance == null) {
			synchronized(BoardDao.class) {
				instance = new BoardDao();
			}
		}
		return instance;
	}
	
	//전체 글 개수를 알아오는 메서드
	public int getArticleCount() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int count = 0;
		try {
			conn = ConnUtil.getConnection();
			pstmt = conn.prepareStatement("select count(*) from BOARD");
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) try {rs.close(); } catch(SQLException e) {}
			if(pstmt != null) try {pstmt.close(); } catch(SQLException e) {}
			if(conn != null) try {conn.close(); } catch(SQLException e) {}
		}
		return count;
	}

	//글 목록을 가져와서 List로 변환하는 메서드
	public List<BoardDto> getArticles(int start, int end){
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<BoardDto> articleList = null;

		try {
			conn = ConnUtil.getConnection();
			String sql = "select * from (select rownum RNUM, NUM, WRITER, EMAIL, SUBJECT, PASS, REGDATE, "
					+ "READCOUNT, REF, STEP, DEPTH, CONTENT, IP from (select * from BOARD order by REF desc, STEP asc))"
					+ "where RNUM >= ? and RNUM <=?";
			pstmt = conn.prepareStatement(sql);
			System.out.println(sql);
			pstmt.setInt(1, start);
			pstmt.setInt(2, end);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				articleList = new ArrayList<BoardDto>(5);
				do {
					BoardDto article = new BoardDto();
					article.setNum(rs.getInt("num"));
					article.setWriter(rs.getString("writer"));
					article.setEmail(rs.getString("email"));
					article.setSubject(rs.getString("subject"));
					article.setPass(rs.getString("pass"));
					article.setRegdate(rs.getTimestamp("regdate"));
					article.setReadcount(rs.getInt("readcount"));
					article.setRef(rs.getInt("ref"));
					article.setStep(rs.getInt("step"));
					article.setDepth(rs.getInt("depth"));
					article.setContent(rs.getString("content"));
					article.setIp(rs.getString("ip"));
					articleList.add(article);
				} while(rs.next());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) try {rs.close();} catch (SQLException e) {}
			if(pstmt != null) try {pstmt.close();} catch (SQLException e) {}
			if(conn != null) try {conn.close();} catch (SQLException e) {}
		}
		return articleList;
	}
	
	//글 저장을 처리하는 메서드
	public void insertArticle(BoardDto article) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int num = article.getNum();	int ref = article.getRef();
		int step = article.getStep();	int depth = article.getDepth();
		int number = 0;	String sql="";
		
		try {
			conn = ConnUtil.getConnection();
			pstmt = conn.prepareStatement("select max(num) from BOARD");
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				number = rs.getInt(1)+1;
			} else { number =1; } 
			if(num != 0) {		//답글일 경우
				sql = "update BOARD set STEP = STEP+1 where REF = ? and STEP > ?";
				pstmt.close();
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, ref);
				pstmt.setInt(2, step);
				pstmt.executeUpdate();
				step = step + 1;
				depth = depth + 1;
			} else {	//새글일 경우
				ref = number;
				step = 0;
				depth = 0;
			}
			//쿼리 작성
			sql = "insert into BOARD "
					+ "(NUM, WRITER, EMAIL, SUBJECT, PASS, REGDATE, REF, STEP, DEPTH, CONTENT, IP, FILENAME, FILESIZE)"
					+ "values(BOARD_SEQ.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, article.getWriter());
			pstmt.setString(2, article.getEmail());
			pstmt.setString(3, article.getSubject());
			pstmt.setString(4, article.getPass());
			pstmt.setTimestamp(5, article.getRegdate());
			pstmt.setInt(6, ref);
			pstmt.setInt(7, step);
			pstmt.setInt(8, depth);
			pstmt.setString(9, article.getContent());
			pstmt.setString(10, article.getIp());
			pstmt.setString(11, article.getFilename());
			pstmt.setInt(12, article.getFilesize());
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) try {rs.close();} catch (SQLException e) {}
			if(pstmt != null) try {pstmt.close();} catch (SQLException e) {}
			if(conn != null) try {conn.close();} catch (SQLException e) {}
		}
	}
	
	//글 내용을 가져오는 메서드
	public BoardDto getArticle(int num) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		BoardDto article = null;
		
		try {
			conn = ConnUtil.getConnection();
			pstmt = conn.prepareStatement("update BOARD set READCOUNT=READCOUNT+1 where NUM = ?");
			pstmt.setInt(1, num);
			pstmt.executeUpdate();
			pstmt.close();
			pstmt = conn.prepareStatement("select * from BOARD where NUM = ?");
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				article = new BoardDto();
				article.setNum(rs.getInt("num"));
				article.setWriter(rs.getString("writer"));
				article.setEmail(rs.getString("email"));
				article.setSubject(rs.getString("subject"));
				article.setPass(rs.getString("pass"));
				article.setRegdate(rs.getTimestamp("regdate"));}
				article.setReadcount(rs.getInt("readcount"));
				article.setRef(rs.getInt("ref"));
				article.setStep(rs.getInt("step"));
				article.setDepth(rs.getInt("depth"));
				article.setContent(rs.getString("content"));
				article.setIp(rs.getString("ip"));
				article.setFilename(rs.getString("filename"));
				article.setFilesize(rs.getInt("filesize"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) try {rs.close();} catch (SQLException e) {}
			if(pstmt != null) try {pstmt.close();} catch (SQLException e) {}
			if(conn != null) try {conn.close();} catch (SQLException e) {}
		}
			return article;
	}
	
	//글 수정을 처리할 글의 세부 데이터를 받아올 수 있는 방법
	public BoardDto updateGetArticle(int num) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		BoardDto article = null;
		
		try {
			conn = ConnUtil.getConnection();
			pstmt = conn.prepareStatement("select * from BOARD where NUM = ?");
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				article = new BoardDto();
				article.setNum(rs.getInt("num"));
				article.setWriter(rs.getString("writer"));
				article.setEmail(rs.getString("email"));
				article.setSubject(rs.getString("subject"));
				article.setPass(rs.getString("pass"));
				article.setRegdate(rs.getTimestamp("regdate"));}
				article.setReadcount(rs.getInt("readcount"));
				article.setRef(rs.getInt("ref"));
				article.setStep(rs.getInt("step"));
				article.setDepth(rs.getInt("depth"));
				article.setContent(rs.getString("content"));
				article.setIp(rs.getString("ip"));
				article.setFilename(rs.getString("filename"));
				article.setFilesize(rs.getInt("filesize"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) try {rs.close();} catch (SQLException e) {}
			if(pstmt != null) try {pstmt.close();} catch (SQLException e) {}
			if(conn != null) try {conn.close();} catch (SQLException e) {}
		}
		return article;
	}
	
	//글 수정 처리할 메서드
	public int updateArticle(BoardDto article) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String dbpasswd = "";
		String sql = "";
		int result = -1;
		try {
			conn = ConnUtil.getConnection();
			pstmt = conn.prepareStatement("select pass from BOARD where NUM = ?");
			pstmt.setInt(1, article.getNum());
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dbpasswd = rs.getString("pass");
				if(dbpasswd.equals(article.getPass())) {
					sql = "update BOARD set WRITER=?, EMAIL=?, SUBJECT=?, CONTENT=?, FILENAME=?, FILESIZE=? where NUM=?";
					pstmt.close();
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, article.getWriter());
					pstmt.setString(2, article.getEmail());
					pstmt.setString(3, article.getSubject());
					pstmt.setString(4, article.getContent());
					pstmt.setString(5, article.getFilename());
					pstmt.setInt(6, article.getFilesize());
					pstmt.setInt(7,	article.getNum());
					pstmt.executeUpdate();
					result = 1;	//수정 성공
				} else {
					result = 0;	//수정 실패
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) try {rs.close();} catch (SQLException e) {}
			if(pstmt != null) try {pstmt.close();} catch (SQLException e) {}
			if(conn != null) try {conn.close();} catch (SQLException e) {}
		}
		return result;
	}
	
	//DB에서 글을 삭제하는 메서드
	public int deleteArticle(int num,String pass) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String filename = null;
		String dbPass="";
		int result = -1;
		try {
			conn = ConnUtil.getConnection();
			pstmt = conn.prepareStatement("select * from BOARD where NUM = ?");
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dbPass = rs.getString("pass");
				filename = rs.getString("filename");
				if(dbPass.equals(pass)) {
					if(!filename.equals("")) {
						File dir=new File("d:\\javastudy\\jspupload");
						File[] files=dir.listFiles();
						for(File f:files) {
							if(f.getName().equals(filename)) {
								f.delete();
							}
						}
					}
					pstmt.close();
					pstmt = conn.prepareStatement("delete from BOARD where NUM=?");
					pstmt.setInt(1, num);
					pstmt.executeUpdate();
					result = 1;	//삭제 성공
				} else { result = 0;} // 비밀번호 불일치
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) try {rs.close();} catch (SQLException e) {}
			if(pstmt != null) try {pstmt.close();} catch (SQLException e) {}
			if(conn != null) try {conn.close();} catch (SQLException e) {}
		}
		return result;
	}
	
	
}
