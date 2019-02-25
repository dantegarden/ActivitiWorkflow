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
<link rel="stylesheet" type="text/css" href="${ctx}/static/css/designer/flowdesign.css"/>
<script src="${ctx}/static/js/designer/jquery.jsPlumb-1.3.16-all-min.js"></script>
<script src="${ctx}/static/js/designer/jquery.contextmenu.r2.js"></script>
<script src="${ctx}/static/js/designer/flowdesign.js"></script>
<script src="${ctx}/static/my/js/manage_design.js"></script>
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
						<li><a href="${ctx}/process/begin">流程起止</a></li>
						<li><a href="${ctx}/process/query">任务查询</a></li>
						<li><a href="${ctx}/process/execute">推进任务</a></li>
						<li class="active"><a href="${ctx}/process/designer">流程设计器</a></li>
					</ul>
				</li>
			</ul>
		</div>

		<div id="content">
			<div id="content-header">
				<h1>流程设计器</h1>

			</div>
			<div id="breadcrumb">
				<a href="#" title="Go to Home" class="tip-bottom"><i
					class="fa fa-home"></i> 工作流测试</a> <a href="#" class="current">流程设计器</a>
			</div>

			<div class="row">
				<div class="col-xs-12">
					<div class="widget-box">
						<div class="widget-title">
							<span class="icon"> <i class="fa fa-th"></i>
							</span>
							<h5>流程设计器</h5>
							<span style="float:right;padding:3px 5px 2px">
								<button type="button" id="btn-load" class="btn btn-success btn-xs">载入流程</button>
								<button type="button" id="btn-export" class="btn btn-info btn-xs">导出结构</button>
								<button type="button" id="btn-save" class="btn btn-purple btn-xs">保存</button>
								<button type="button" id="btn-clean" class="btn btn-danger btn-xs">清除</button>
							</span>
						</div>
						<div class="widget-content nopadding">
							<div class="container mini-layout" id="flowdesign_canvas">
							</div>
							
							<!--contextmenu div-->
							<div id="processMenu" style="display:none;">
							  <ul>
							    <li id="pm_begin"><i class="icon-play"></i>&nbsp;<span class="_label">设为第一步</span></li>
							    <li id="pmAttribute"><i class="icon-cog"></i>&nbsp;<span class="_label">配置角色</span></li>
							    <li id="pmForm"><i class="icon-th"></i>&nbsp;<span class="_label">表单字段</span></li>
							    <li id="pmJudge"><i class="icon-share-alt"></i>&nbsp;<span class="_label">转出条件</span></li>
							    <li id="pmDelete"><i class="icon-trash"></i>&nbsp;<span class="_label">删除</span></li>
							  </ul>
							</div>
							<div id="canvasMenu" style="display:none;">
							  <ul>
							    <li id="cmAdd"><i class="icon-plus"></i>&nbsp;<span class="_label">添加步骤</span></li>
							    <li id="cmRefresh"><i class="icon-refresh"></i>&nbsp;<span class="_label">刷新</span></li>
							  </ul>
							</div>
						</div>
						
					</div><!-- widget end -->
					
					
					
				</div>
				
			</div>
		</div>
				
		<div id="cmAddModal" class="modal fade in" aria-hidden="false" style="display: none;">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button data-dismiss="modal" class="close" type="button">×</button>
						<h3>添加步骤</h3>
					</div>
					<div class="modal-body">
						<form action="#" id="cmAddForm" method="post" class="form-horizontal">
							<div class="form-group">
								<label class="col-sm-3 col-md-3 col-lg-2 control-label">节点类型</label>
								<div class="col-sm-9 col-md-9 col-lg-10">
									<select name="block_type" class="form-control">
									  <option value ="task">任务节点</option>
									  <option value ="gateway">网关节点</option>
									</select>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 col-md-3 col-lg-2 control-label">任务类型</label>
								<div class="col-sm-9 col-md-9 col-lg-10">
									<select name="task_type" class="form-control">
									  <option value ="assign-one">固定人审批</option>
									  <option value ="single-user">多人汇签</option>
									  <option value ="single-role">固定角色汇签</option>
									  <option value ="mult-role">多角色并签</option>
									  <option value ="mult-user">多人并签</option>
									</select>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 col-md-3 col-lg-2 control-label">节点名称</label>
								<div class="col-sm-9 col-md-9 col-lg-10">
									<input type="text" name="task_name" class="form-control input-sm">
								</div>
							</div>
						</form>
					</div>
					<div class="modal-footer">
						<button id="submitCmAdd" type="button" class="btn btn-primary">确定</button>
					</div>
				</div>
			</div>							
		</div>
		
		<div id="cmAttributeModal" class="modal fade in" aria-hidden="false" style="display: none;">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button data-dismiss="modal" class="close" type="button">×</button>
						<h3>配置角色</h3>
					</div>
					<div class="modal-body">
						<form action="#" id="cmAttributeForm" method="post" class="form-horizontal">
							<input type="hidden" name="task_id" />
							<div class="form-group">
								<label class="col-sm-3 col-md-3 col-lg-2 control-label">处理角色</label>
								<div class="col-sm-9 col-md-9 col-lg-10">
									<select name="task_role" class="form-control"></select>
								</div>
							</div>
						</form>
					</div>
					<div class="modal-footer">
						<button id="submitCmAttribute" type="button" class="btn btn-primary">确定</button>
					</div>
				</div>
			</div>							
		</div>
		
		<div id="cmFormModal" class="modal fade in" aria-hidden="false" style="display: none;">
			<div class="modal-lg modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button data-dismiss="modal" class="close" type="button">×</button>
						<h3>表单字段</h3>
					</div>
					<div class="modal-body">
						<form action="#" id="cmFormForm" method="post" class="form-horizontal">
							 <input type="hidden" name="task_id" />
							 <span style="float:right;">
							 	<button class="btn btn-success btn-xs" type="button" id="cmFormAdd">添加</button>
							 </span>
							 <table class="table table-bordered table-striped table-hover"> 
							  	<thead> 
								  <tr> 
								   <th>字段名称</th> 
								   <th>控件类型</th> 
								   <th width="10%">必填</th>
								   <th>操作</th>  
								  </tr> 
							  	</thead>
							  	<tbody>
							  	</tbody>
							 </table> 
						</form>
					</div>
					<div class="modal-footer">
						<button id="submitCmForm" type="button" class="btn btn-primary">确定</button>
					</div>
				</div>
			</div>							
		</div>
		
		<div id="cmJudgeModal" class="modal fade in" aria-hidden="false" style="display: none;">
			<div class="modal-lg modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button data-dismiss="modal" class="close" type="button">×</button>
						<h3>转出条件</h3>
					</div>
					<div class="modal-body">
						<form action="#" id="cmJudgeForm" method="post" class="form-horizontal">
							 <input type="hidden" name="task_id" />
							 <table class="table table-bordered table-striped table-hover"> 
							  	<thead> 
								  <tr> 
								   <th>分支路线</th> 
								   <th>关键字段</th> 
								   <th width="15%">表达式</th>
								   <th>值</th>  
								  </tr> 
							  	</thead>
							  	<tbody>
							  	</tbody>
							 </table> 
						</form>
					</div>
					<div class="modal-footer">
						<button id="submitCmJudge" type="button" class="btn btn-primary">确定</button>
					</div>
				</div>
			</div>							
		</div>
		
		<div id="btnSaveModal" class="modal fade in" aria-hidden="false" style="display: none;">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button data-dismiss="modal" class="close" type="button">×</button>
						<h3>保存设计</h3>
					</div>
					<div class="modal-body">
						<form action="#" id="btnSaveForm" method="post" class="form-horizontal">
							<div class="form-group">
								<label class="col-sm-3 col-md-3 col-lg-2 control-label">流程名称</label>
								<div class="col-sm-9 col-md-9 col-lg-10">
									<input type="text" name="process_name" class="form-control" />
								</div>
							</div>
						</form>
					</div>
					<div class="modal-footer">
						<button id="submitBtnSave" type="button" class="btn btn-primary">确定</button>
					</div>
				</div>
			</div>							
		</div>
		
		
		<div id="loadProcessModal" class="modal fade in" aria-hidden="false" style="display: none;">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button data-dismiss="modal" class="close" type="button">×</button>
						<h3>载入流程设计</h3>
					</div>
					<div class="modal-body">
						<form action="#" id="loadProcessForm" method="post" class="form-horizontal">
							<div class="form-group">
								<label class="col-sm-3 col-md-3 col-lg-2 control-label">选择流程</label>
								<div class="col-sm-9 col-md-9 col-lg-10">
									<select name="process_defination_id" class="form-control"></select>
								</div>
							</div>
						</form>
					</div>
					<div class="modal-footer">
						<button id="submitLoadProcess" type="button" class="btn btn-primary">确定</button>
					</div>
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
