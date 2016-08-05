package org.tss.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "streamTwitz")
@SessionScoped
public class StreamTwitz implements Serializable {

	private static final long serialVersionUID = 3120515161257358014L;
	private Properties connectionProperties = new Properties();
	private ResultSet resultSet;
	private List<TweetBean> resultAsArrayList;

	private String searchTerm = "albania"; 

	public List<TweetBean> initz() {
		String query = "SELECT From_User_Name, Text FROM [Tweets] WHERE SearchTerms= '" + searchTerm + "'";
		connectionProperties = getPropValuesFromFile();
		getResults(query);
		setResultAsArrayList(mapResultSetToArrayList());
		
		return getResultAsArrayList();
	}

	private Properties getPropValuesFromFile() {
		InputStream inputStream = null;
		Properties properties = new Properties();
		try {
			inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");
			properties.load(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return properties;
	}

	private List<TweetBean> mapResultSetToArrayList() {
		List<TweetBean> resultList = new ArrayList<TweetBean>();
		try {
			while (resultSet.next()) {
				TweetBean tweet = new TweetBean();
				tweet.setText(resultSet.getString(2));
				tweet.setUsername(resultSet.getString(1));
				resultList.add(tweet);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultList;
	}

	private void getResults(String query) {
		try {
			Class.forName("cdata.jdbc.twitter.TwitterDriver");
			Connection conn = DriverManager.getConnection("jdbc:twitter:", connectionProperties);
			Statement stmt = conn.createStatement();
			stmt.execute(query);
			resultSet = stmt.getResultSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<TweetBean> getResultAsArrayList() {
		return resultAsArrayList;
	}

	public void setResultAsArrayList(List<TweetBean> resultAsArrayList) {
		this.resultAsArrayList = resultAsArrayList;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}
}