$(function(){
	
	initTable();
	initUninstallBtn();
	initDetailBtn();
	initAddDeployBtn();
	initSelectProcessDefination();
	initSubmitAddDeployBtn();
});

var detailTable = null;

function initTable(){
	window.activiti_deploy_table = $('#activiti-deploy-table').dataTable({
        "sAjaxSource": CTX + "/process/getDeployments",
        "aoColumns": [
                      { "mData": "deploymentId" },
                      { "mData": "name" },
                      { "mData": "deploymentTime",
//                    	"mRender": function(data, type, rowdata) {
//                    		return new Date(data).toLocaleString();}
                      },
                      { "mData": "deploymentId",
                    	"mRender": function(data, type, rowdata) {
                            return "<button class='btn btn-inverse' role='uninstall' data-id='"+data+"'>卸载</button> " +
                                   "<button class='btn btn-info' role='detail' data-id='"+data+"'>详情</button>";
                       }}
                  ],
        "bFilter": false,
        "bAutoWidth": true,
        "bLengthChange": false,
        "bJQueryUI": true,
		"sPaginationType": "full_numbers",
		"sDom": '<""l>t<"F"fp>',
        "bFilter": true,
        "bServerSide": true, // 启用服务端模式
	    "bDestroy" : true,
	    "bSort": false, // 禁用客户端排序(没有必要)
        "oLanguage": {
            "oPaginate":{"sFirst":"首页","sLast":"尾页","sNext":"下一页","sPrevious":"上一页"},
			"sLengthMenu": "显示 _MENU_ 条记录",
			"sSearch": "查询：",
			"sInfo": "显示第 _START_ - _END_ 条记录，共 _TOTAL_ 条",
			"sInfoEmpty": " ",
			"sZeroRecords": "没有符合条件的记录",
			"sEmptyTable": "没有符合条件的记录"
		}
    });
}

function initUninstallBtn(){
	$(document).on("click","button[role='uninstall']",function(e){
		var $this = $(this);
		$.post(CTX + "/process/findActiveProcInstance",{deploymentId: $this.data("id")},function(data){
			if(data.success){
				if (bootbox.confirm({
		             message: "目前有"+data.data+"个活动的流程实例，确定要卸载该数据包吗 ?",
		             buttons: {
		                 confirm: {
		                     label: "<i class='ace-icon fa fa-trash-o bigger-110'></i> 确定",
		                     className: "btn-danger btn-sm"
		                 },
		                 cancel: {
		                     label: "<i class='ace-icon fa fa-times bigger-110'></i> 取消",
		                     className: "btn-sm"
		                 }
		             },
		             callback: function(result) {
		                 if (result) {
		                	 $.ajax({
		                         cache: true,
		                         type: "POST",
		                         url: CTX + "/process/dropDeployment",
		                         data: {
		                             "deploymentId" : $this.data("id")
		                         },
		                         async: false,
		                         error: function(request) {
		                             alert("出错" + request)
		                         },
		                         success: function(data) {
		                         	bootbox.alert("卸载成功！");
		                         	$("#activiti-deploy-table").dataTable().fnDraw();
		                         }
		                     });
		                 }
		             }
		         }));
			}else{
				bootbox.alert(data.msg);
			}
		});
		
		 
		
	});
}

function initDetailBtn(){
	$(document).on("click","button[role='detail']",function(e){
		var $this = $(this);
		initDetailTable($this.data("id"));
		$("#myModal div.modal-header h4").html("部署包详情");
        $("#myModal").modal("show");
	});
}

function initDetailTable(id){
	$("#myModal-hidden").val(id);
	if(detailTable){
		$('#activiti-deploy-detail-table').dataTable().fnDraw();
	}else{
		console.log("!")
		detailTable = $('#activiti-deploy-detail-table').dataTable({
			"sAjaxSource": CTX + "/process/getDeployDetail" ,
			"aoColumns": [
			              { "mData": "name"},
			              { "mData": "key" },
			              { "mData": "prodefinedId" },
			              { "mData": "resource",
			            	  "mRender" : function(data, type, rowdata) {
			            		  if(data){ 
			            			  var a = data.split("&&");
			            			  if(a.length>1){
			            				  return a.join("<br />")
			            			  }else{
			            				  return data;
			            			  }
			            		  }else{
			            			  return "无记录";
			            		  }
			            		  return data;
			            	  }}
			              ],
			              "fnServerParams": function(aoData) {
		                        aoData.push({
		                            "name": "id",
		                            "value": $('#myModal-hidden').val()
		                        });
		                  },
			              "bFilter": false,
			              "bAutoWidth": true,
			              "bLengthChange": false,
			              "bJQueryUI": true,
			              "sPaginationType": "full_numbers",
			              "sDom": '<""l>t<"F"fp>',
			              "bFilter": true,
			              "bServerSide": true, // 启用服务端模式
			              "bDestroy" : true,
			              "bSort": false, // 禁用客户端排序(没有必要)
			              "oLanguage": {
			            	  "oPaginate":{"sFirst":"首页","sLast":"尾页","sNext":"下一页","sPrevious":"上一页"},
			            	  "sLengthMenu": "显示 _MENU_ 条记录",
			            	  "sSearch": "查询：",
			            	  "sInfo": "显示第 _START_ - _END_ 条记录，共 _TOTAL_ 条",
			            	  "sInfoEmpty": " ",
			            	  "sZeroRecords": "没有符合条件的记录",
			            	  "sEmptyTable": "没有符合条件的记录"
			              }
		});
	}
}

function initSelectProcessDefination(){
	$.post(CTX+"/designer/findProcessDefinations",null,function(r){
		if(r.success){
			$("#addDeployForm select[name='process_defination_id']").html('');
			for(var i in r.data){
				$("#addDeployForm select[name='process_defination_id']").append("<option value='"+r.data[i].id+"'>" + r.data[i].processDefinationName + "</option>");
			}
		}
	});
}

function initAddDeployBtn(){
	$(document).on("click","#btn-add",function(e){
		$("#addDeployModal").modal('show');
	})
} 

function initSubmitAddDeployBtn(){
	$(document).on("click","#submitAddDeploy",function(e){
		$.post(CTX+"/designer/addDeployment",{
			id: $("#addDeployForm select[name='process_defination_id']").val()
		},function(r){
			if(r.success){
				$("#addDeployModal").modal('hide');
				bootbox.alert("部署成功！");
				window.activiti_deploy_table.fnDraw();
			}
		});
		
	})
} 