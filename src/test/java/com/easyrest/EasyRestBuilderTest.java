package com.easyrest;

import org.junit.Test;

import com.easyrest.EasyRestBuilder;



public class EasyRestBuilderTest {

	@Test
	public void getSkillbyId() {
		
		// How can I remove the param in the constructor
		EasyRestBuilder<GitHubClient> builder = new EasyRestBuilder<GitHubClient>(GitHubClient.class);
		GitHubClient gitHubClient = builder.createClient("https://api.github.com");
		
		// get users
		String users = gitHubClient.getUsers();
		System.out.println(users);
		
		// get user
		String user = gitHubClient.getUserByUsername("defunkt");
		System.out.println(user);
	}
}
