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
<script src="${ctx}/static/my/js/manage_main.js"></script>

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
				<li class="active"><a href="${ctx}/process"><i class="fa fa-home"></i> <span>工作流包部署情况</span></a></li>
				<li class="submenu">
					<a href="#"><i class="fa fa-flask"></i> <span>工作流测试</span> <i class="arrow fa fa-chevron-right"></i></a>
					<ul>
						<li><a href="${ctx}/process/begin">流程起止</a></li>
						<li><a href="${ctx}/process/query">任务查询</a></li>
						<li><a href="${ctx}/process/execute">推进任务</a></li>
						<li><a href="${ctx}/process/designer">流程设计器</a></li>
					</ul>
				</li>
			</ul>
		</div>

		<div id="content">
			<div id="content-header">
				<h1>工作流包部署情况</h1>

			</div>
			<div id="breadcrumb">
				<a href="#" title="Go to Home" class="tip-bottom"><i
					class="fa fa-home"></i> 首页</a> <a href="#" class="current">工作流包部署情况</a>
			</div>

			<div class="row">
				<div class="col-xs-12">
					<div class="widget-box">
						<div class="widget-title">
							<span class="icon"> <i class="fa fa-th"></i>
							</span>
							<h5>当前部署的包</h5>
							<span style="float:right;padding:3px 5px 2px">
								<button type="button" id="btn-add" class="btn btn-info btn-xs">部署新流程</button>
							</span>
						</div>
						<div class="widget-content nopadding">
							<table id="activiti-deploy-table" class="table table-bordered table-striped table-hover">
								<thead>
									<tr>
										<th>ID</th>
										<th>包名</th>
										<th>部署时间</th>
										<th>操作</th>
									</tr>
								</thead>
								<tbody>
									
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
		</div>
		
		
		<div id="myModal" class="modal fade" tabindex="-1" role="basic"
            aria-hidden="true" style=";" data-backdrop="static">
              <div class="modal-dialog" style="width:1100px;">
                <div class="modal-content">
                  <form id="myModalForm" class="form-horizontal" role="form" method="post">
                  	<input type="hidden" id="myModal-hidden" />
                    <div class="modal-header">
                      <button type="button" class="close" data-dismiss="modal">
                        &times;
                      </button>
                      <h4 class="blue bigger">
                      </h4>
                    </div>
                    <div class="modal-body">
                      <div class="row">
                        <div class="col-xs-12">
                          	<table id="activiti-deploy-detail-table" class="table table-bordered table-striped table-hover">
                          		<thead>
									<tr>
										<th width="200px">流程名</th>
										<th width="10%">流程代码</th>
										<th width="10%">流程版本</th>
										<th>资源文件</th>
									</tr>
								</thead>
                          	</table>
                        </div>
                      </div>
                    </div>
                    <div class="modal-footer">
                      <button class="btn btn-sm btn-default" data-dismiss="modal" type="button">
                        <i class="ace-icon fa fa-times">
                        </i>取消
                      </button>
                    </div>
                  </form>
                </div>
              </div>
        </div>
		
		<div id="addDeployModal" class="modal fade in" aria-hidden="false" style="display: none;">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button data-dismiss="modal" class="close" type="button">×</button>
						<h3>部署新流程</h3>
					</div>
					<div class="modal-body">
						<form action="#" id="addDeployForm" method="post" class="form-horizontal">
							<div class="form-group">
								<label class="col-sm-3 col-md-3 col-lg-2 control-label">选择流程</label>
								<div class="col-sm-9 col-md-9 col-lg-10">
									<select name="process_defination_id" class="form-control"></select>
								</div>
							</div>
						</form>
					</div>
					<div class="modal-footer">
						<button id="submitAddDeploy" type="button" class="btn btn-primary">确定</button>
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
