package com.itheima.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

import com.itheima.domain.User;
import com.itheima.service.UserService;

/**
 * 自定义Realm
 * （1）AuthenticatingRealm：shiro中的用于进行认证的领域，实现doGetAuthentcationInfo方法实现用户登录时的认证逻辑；
 * （2）AuthorizingRealm：shiro中用于授权的领域，实现doGetAuthrozitionInfo方法实现用户的授权逻辑，AuthorizingRealm继承了AuthenticatingRealm，
 * 所以在实际使用中主要用到的就是这个AuthenticatingRealm类；
 * （3）AuthenticatingRealm、AuthorizingRealm这两个类都是shiro中提供了一些线程的realm接口
 * （4）在与spring整合项目中，shiro的SecurityManager会自动调用这两个方法，从而实现认证和授权，可以结合shiro的CacheManager将认证和授权信息保存在缓存中，
 * 这样可以提高系统的处理效率。    
 *
 */
public class UserRealm extends AuthorizingRealm{

	@Autowired
	private UserService userSerivce;

	/**
	 * 执行认证逻辑
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken arg0) throws AuthenticationException {
		System.out.println("执行认证逻辑");

		//编写shiro判断逻辑，判断用户名和密码
		//1.判断用户名  token中的用户信息是登录时候传进来的
		UsernamePasswordToken token = (UsernamePasswordToken)arg0;

		User user = userSerivce.findByName(token.getUsername());

		if(user==null){
			//用户名不存在
			return null;//shiro底层会抛出UnKnowAccountException
		}

		//2.判断密码
		//第二个字段是user.getPassword()，注意这里是指从数据库中获取的password。第三个字段是realm，即当前realm的名称。
		//这块对比逻辑是先对比username，但是username肯定是相等的，所以真正对比的是password。
		//从这里传入的password（这里是从数据库获取的）和token（filter中登录时生成的）中的password做对比，如果相同就允许登录，
		// 不相同就抛出IncorrectCredentialsException异常。
		//如果认证不通过，就不会执行下面的授权方法了
		return new SimpleAuthenticationInfo(user,user.getPassword(),"");
	}

	/**
	 * 执行授权逻辑
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection arg0) {

	    //doGetAuthorizationInfo方法可能会执行多次，权限判断次数多少，就会执行多少次
		System.out.println("执行授权逻辑1");
		System.out.println("执行授权逻辑2");
		
		//给资源进行授权
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		
		//添加资源的授权字符串
		//info.addStringPermission("user:add");
		
		//到数据库查询当前登录用户的授权字符串
		//获取当前登录用户
		Subject subject = SecurityUtils.getSubject();
		User user = (User)subject.getPrincipal();
		User dbUser = userSerivce.findById(user.getId());
		
		info.addStringPermission(dbUser.getPerms());
		
		return info;
	}
}
