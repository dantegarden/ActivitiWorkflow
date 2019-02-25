<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/commons/taglibs.jsp"%>
<%@include file="/WEB-INF/commons/pre-include.jsp"%>

<!DOCTYPE html>
<html>
<head>
<base href="<%=basePath%>">

<title>DVT Activiti WEB-UI</title>

<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<script src="${ctx}/static/my/js/manage_begin.js"></script>

</head>

<body data-color="grey" class="flat">
	<div id="wrapper">
		<div id="header">
			<h1>
				<a href="#">Unicorn Admin</a>
			</h1>
			<a id="menu-trigger" href="#"><i class="fa fa-align-justify"></i></a>
		</div>

		<div id="sidebar">
			<div id="search">
				<input type="text" placeholder="Search here..."/><button type="submit" class="tip-right" title="Search"><i class="fa fa-search"></i></button>
			</div>
			<ul>
				<li class=""><a href="${ctx}/process"><i class="fa fa-home"></i> <span>工作流包部署情况</span></a></li>
				<li class="submenu">
					<a href="#"><i class="fa fa-flask"></i> <span>工作流测试</span> <i class="arrow fa fa-chevron-right"></i></a>
					<ul>
						<li class="active"><a href="${ctx}/process/begin">流程起止</a></li>
						<li><a href="${ctx}/process/query">任务查询</a></li>
						<li><a href="${ctx}/process/execute">推进任务</a></li>
						<li><a href="${ctx}/process/designer">流程设计器</a></li>
					</ul>
				</li>
			</ul>
		</div>

		<div id="content">
			<div id="content-header">
				<h1>任务起止</h1>

			</div>
			<div id="breadcrumb">
				<a href="#" title="Go to Home" class="tip-bottom"><i
					class="fa fa-home"></i> 工作流测试</a> <a href="#" class="current">任务起止</a>
			</div>

			<div class="row">
				<div class="col-xs-12">
					<div class="widget-box">
						<div class="widget-title">
							<span class="icon"> <i class="fa fa-th"></i>
							</span>
							<h5>启动流程</h5>
							<span style="float:right;padding:3px 5px 2px">
								<button type="button" id="btn-begin" class="btn btn-purple btn-xs">启动</button>
							</span>
						</div>
						<div class="widget-content nopadding">
							<form action="#" id="form-startWorkflow" method="post" class="form-horizontal">
								<div class="form-group">
									<div class="col-sm-12 col-md-12 col-lg-11">
										<div class="row">
											<div class="col-md-4">
												<input type="text" name="processDefinitionKey" class="form-control input-sm">
												<span class="help-block text-center">流程类型代码</span>
											</div>
											<div class="col-md-4">
												<input type="text" name="userId" class="form-control input-sm">
												<span class="help-block text-center">发起用户ID</span>
											</div>
											<div class="col-md-4">
												<input type="text" name="participantRoles" class="form-control input-sm">
												<span class="help-block text-center">下环节参与角色</span>
											</div>
									    </div>
									</div>
								</div>
								<div class="form-group">
									<div class="col-sm-12 col-md-12 col-lg-11">
										<div class="row">
											<div class="col-md-4">
												<input type="text" name="nextUserId" class="form-control input-sm">
												<span class="help-block text-center">指定下环节委办人</span>
											</div>
											<div class="col-md-4">
												<input type="text" name="nextRoleId" class="form-control input-sm">
												<span class="help-block text-center">指定下环节角色</span>
											</div>
											<div class="col-md-4">
												<input type="text" name="participantUsers" class="form-control input-sm">
												<span class="help-block text-center">下环节参与用户</span>
											</div>
									    </div>
									</div>
								</div>
								<div class="form-group">
									<div class="col-sm-12 col-md-12 col-lg-11">
										<div class="row">
											<div class="col-md-4">
												<input type="text" name="judge" class="form-control input-sm">
												<span class="help-block text-center">选路</span>
											</div>
									    </div>
									</div>
								</div>
							</form>
						</div>
						
					</div><!-- widget end -->
					
					
					<div class="widget-box">
						<div class="widget-title">
							<span class="icon"> <i class="fa fa-th"></i>
							</span>
							<h5>终止流程</h5>
							<span style="float:right;padding:3px 5px 2px">
								<button type="button" id="btn-end" class="btn btn-purple btn-xs">终止</button>
							</span>
						</div>
						<div class="widget-content nopadding">
							<form action="#"  method="post" class="form-horizontal">
								<div class="form-group">
									<label class="col-sm-3 col-md-3 col-lg-2 control-label">流程实例ID</label>
									<div class="col-sm-9 col-md-9 col-lg-10">
										<input type="text" id="processInstanceId" class="form-control input-sm">
									</div>
								</div>
							</form>
						</div>
						
					</div><!-- widget end -->
					
				</div>
				
				
				
			</div>
		</div>
				
		
		
		<div class="row">
			<div id="footer" class="col-xs-12">
					2012 - 2013 &copy; Unicorn Admin. Brought to you by <a href="https://wrapbootstrap.com/user/diablo9983">diablo9983</a>
			</div>
		</div>
	</div>
</body>
</html>
