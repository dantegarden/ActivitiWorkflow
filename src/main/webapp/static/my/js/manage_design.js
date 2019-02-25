$(function(){
	/***startpoint 开始 单人
	 * mult-user   多实例 人
	 * mult-role   多实例 角色
	 * single-user 多人汇签
	 * single-role 固定角色汇签
	 * assign-one  固定单人
	 * process_to没值就是endpoint
	 */
	window.processData = {};
	
	window.processForm = [];
	
	window.processJudge = [];
	
	var flowDesignOptions = {
	    "processData":window.processData,
	    "canvasMenus": {
            "cmAdd": function(t) {
                $("#cmAddModal").modal("show");
            },
            "cmRefresh":function(t){
                location.reload();//_canvas.refresh();
            }
	    },
	    "processMenus":{
	    	"pm_begin":function(t){
	    		var activeId = _canvas.getActiveId();//右键当前的ID
	    		if($("#task"+activeId).attr("task_type")=="assign-one"){
	    			var workflowObj = _canvas.exportJson();
		    		for(var i=0;i<workflowObj.blocks.length;i++){
		    			var preblock = workflowObj.blocks[i];
		    			if(preblock.task_type=="startpoint"){
		    				$("#"+preblock.task_id).attr("task_type","assign-one");
		    				$("#"+preblock.task_id).find("span i").attr("class","fa fa-male icon-white");
		    				$("#"+preblock.task_id).find("span").removeClass("badge-info");
		    				$("#"+preblock.task_id).find("span").addClass("badge-inverse")
		    			}
		    			$.each(jsPlumb.getConnections(), function (idx, connection) {
		    				if(connection.targetId=="task"+activeId){
		    					jsPlumb.detach(connection);
		    				}
		    		 	});
		    		}
		    		$("#task"+activeId).attr("task_type","startpoint");
		    		$("#task"+activeId).find("span i").attr("class","fa fa-pencil icon-white");
		    		$("#task"+activeId).find("span").removeClass("badge-inverse");
		    		$("#task"+activeId).find("span").addClass("badge-info")
	    		}else if($("#task"+activeId).attr("task_type")=="startpoint"){
	    			bootbox.alert("该节点已经是流程的起点了！");
	    		}else{
	    			bootbox.alert("只有任务类型是固定人的环节才可指定为起点！");
	    		}
	    		
	    	},
	    	"pmAttribute":function(t){//配置角色
	    		var activeId = _canvas.getActiveId();//右键当前的ID
	    		if($("#task"+activeId).attr("task_type")=="single-role"){
	    			if($("#task"+activeId).attr("process_role")){
	    				$("#cmAttributeForm select[name='task_role']").val($("#task"+activeId).attr("process_role"));
	    			}
	    			$("#cmAttributeForm input[name='task_id']").val("task"+activeId);
	    			$("#cmAttributeModal").modal("show");
	    		}else{
	    			bootbox.alert("只有任务类型是固定角色汇签的环节才可以配置角色！");
	    		}
	    	},
	    	"pmForm":function(t){//表单项
	    		 var activeId = _canvas.getActiveId();//右键当前的ID
	    		 $("#cmFormForm input[name='task_id']").val("task"+activeId);
	    		 activeForm = findObjFromArray(processForm,"task"+activeId);
	    		 var tr = "";
	    		 if(activeForm){
	    			 for(var i in activeForm){
	    				 tr += "<tr editable='false'><td><input type='text' class='form-control' value='"+activeForm[i].name+"' style='display:none;'/><span>"+activeForm[i].name+"</span></td><td>";
	    				 var $option = $("<select class='form-control' style='display:none;'>" 
	    						+ "<option value='input'>输入框</option>"
	    						+ "<option value='select'>下拉选择</option>"
	    						+ "<option value='radio'>单选</option>"
	    						+ "<option value='checkbox'>多选</option>"
	    						+ "<option value='textarea'>文本域</option>"
	    						+ "<option value='file'>上传</option>"
	    						+ "</select>");
	    				 $option.val(activeForm[i].type);
	    				 
	    				 tr +=  $option.prop("outerHTML") + "<span>"+ $option.find("option:selected").text() + "</span>" + "</td><td>";
	    				 var $checkbox = $("<input name='isrequired' type='checkbox' class='form-control' value='' "+ (activeForm[i].isRequire?"checked='checked'":"") +" style='display:none;'/>");
	    				 tr += $checkbox.prop("outerHTML") + "<span>"+ (activeForm[i].isRequire?"是":"否") + "</span>"
	    				 + "</td><td align='center'><button type='button' disabled='disabled' role='confirm' class='btn btn-xs btn-success'><i class='fa fa-check' title='确定'></i></button>" +
	    							  "&nbsp;<button type='button' role='modify'  class='btn btn-xs btn-info'><i class='fa fa-cog' title='修改'></i></button>" +
	    							  "&nbsp;<button type='button' role='delete' class='btn btn-xs btn-danger'><i class='fa fa-eraser' title='删除'></i></button>" +
	    						  "</td><tr>";
	    			 }
	    			 $("#cmFormForm table tbody").html(tr);
	    		 }else{
	    			 $("#cmFormForm table tbody").html(tr);
	    			 $("#cmFormAdd").click();
	    		 }
	    		 $("#cmFormModal").modal("show");         
	    	},
	    	"pmJudge":function(t){
	    		var activeId = _canvas.getActiveId();
	    		var conns = _canvas.getConnBySource("task"+activeId);
	    		if(conns.length==1 && conns[0].to_id.startWith("gateway")){//有一个出的线，出到网关才能配
	    			if(setJudge("task"+activeId, conns[0].to_id)){
	    				$("#cmJudgeForm input[name='task_id']").val("task"+activeId);
	    				$("#cmJudgeModal").modal("show");
	    			}else{
	    				bootbox.alert("该节点的网关没有分支输出");
	    			}
	    		}else{
	    			bootbox.alert("该节点没有可用网关来获得分支");
	    		}
	    	},
	    	"pmDelete":function(t){
                if(confirm("你确定删除步骤吗？")){
                      var activeId = _canvas.getActiveId();//右键当前的ID
                      _canvas.delProcess(activeId);
                }
            }
	    }
	}
	var _canvas = $("#flowdesign_canvas").Flowdesign(flowDesignOptions);
	/**————————————————————————————————————清除————————————————————————————————————***/
	//清除画布
	$(document).on("click","#btn-clean",function(e){
		if(confirm("你确定清空画板吗?")){
			_canvas.refresh();
//			$("#flowdesign_canvas").html('');
			$("#flowdesign_canvas div").each(function(i,e){
				if($(this).hasClass("process-step"))
				_canvas.delProcess($(this).attr("process_id"));
			});
			window.processData = {};
			window.processForm = [];
			window.processJudge = [];
//			flowDesignOptions.processData = {};
//			_canvas = $("#flowdesign_canvas").Flowdesign({});
		}
	});
	
	/**————————————————————————————————————节点添加————————————————————————————————————***/
	$(document).on("change","#cmAddForm select[name='block_type']",function(e){
		if($(this).val() == "task"){
			$("#cmAddForm").find("div.form-group:eq(1)").show();
		}else if($(this).val() == "gateway"){
			$("#cmAddForm").find("div.form-group:eq(1)").hide();
		}
	});
	
	$(document).on("click","#submitCmAdd",function(e){
		var mLeft = $("#jqContextMenu").css("left");
	    var mTop = $("#jqContextMenu").css("top");
		//弹出modal，填写节点类型(任务节点、网关) 任务节点的类型  名称  提交表单添加节点到canvas
	    _canvas.addProcess({id:maxBlockId("task"),
	    					block_type: $("#cmAddForm").find("select[name='block_type']").val(),
	    					task_type: $("#cmAddForm").find("select[name='task_type']").val(),
	    					task_name:  $("#cmAddForm").find("input[name='task_name']").val(),
	    					left:mLeft,top:mTop});
	    $("#cmAddModal").modal("hide");
	});
	/**————————————————————————————————————配置角色————————————————————————————————————***/
	$.post(CTX+"/designer/findGroups",null,function(r){
		if(r.success){
			for(var g in r.data){
				$("#cmAttributeForm select[name='task_role']").append("<option value='"+r.data[g].id+"'>"+r.data[g].name+"</option>");
			}
		}
	})
	$(document).on("click","#submitCmAttribute",function(e){
		var task_role = $("#cmAttributeForm select[name='task_role']").val();
		$("#"+$("#cmAttributeForm input[name='task_id']").val()).attr("process_role",task_role);
		$("#cmAttributeModal").modal("hide");
	});
	/**————————————————————————————————————表单字段————————————————————————————————————***/
	/**添加一行*/
	$(document).on("click","#cmFormAdd",function(e){
		var html_tr = "<tr editable='true'><td><input type='text' class='form-control' /></td>"
					+ "<td><select class='form-control'>" 
					+ "<option value='input'>输入框</option>"
					+ "<option value='select'>下拉选择</option>"
					+ "<option value='radio'>单选</option>"
					+ "<option value='checkbox'>多选</option>"
					+ "<option value='textarea'>文本域</option>"
					+ "<option value='file'>上传</option>"
					+ "</select></td>"
					+ "<td><input name='isrequired' type='checkbox' class='form-control' value='' /></td>"
					+ "<td align='center'><button type='button' role='confirm' class='btn btn-xs btn-success'><i class='fa fa-check' title='确定'></i></button>" +
						  "&nbsp;<button type='button' role='modify' disabled='disabled' class='btn btn-xs btn-info'><i class='fa fa-cog' title='修改'></i></button>" +
						  "&nbsp;<button type='button' role='delete' class='btn btn-xs btn-danger'><i class='fa fa-eraser' title='删除'></i></button>" +
					  "</td><tr>";
		$("#cmFormForm table tbody").append(html_tr);
	});
	/**确认*/
	$(document).on("click","#cmFormForm tr button[role='confirm']",function(e){
		$(this).closest("td").find("button").prop("disabled",false);
		$(this).prop("disabled",true);
		var $tr = $(this).closest("tr");
		$tr.find("input:text").each(function(){
			$(this).hide();
			$(this).closest("td").append("<span>"+$(this).val()+"</span>");
		});
		$tr.find("select").each(function(){
			$(this).hide();
			$(this).closest("td").append("<span>"+$(this).find("option:selected").text()+"</span>");
		});
		$tr.find("input:checkbox").each(function(){
			$(this).hide();
			var $span = $("<span></span>").append($(this).is(':checked')?"是":"否" + "</span>")
			$(this).closest("td").append($span);
		});
		$tr.attr("editable","false");
	});
	/**修改*/
	$(document).on("click","#cmFormForm tr button[role='modify']",function(e){
		$(this).closest("td").find("button").prop("disabled",false);
		$(this).prop("disabled",true);
		var $tr = $(this).closest("tr");
		$tr.find("td span").remove();
		$tr.attr('editable','true');
		$tr.find("td input,select").show();
	});
	/**删除*/
	$(document).on("click","#cmFormForm tr button[role='delete']",function(e){
		$(this).closest("tr").remove();
	});
	/**确定*/
	$(document).on("click","#submitCmForm",function(e){
		var task = {};
		task.task_id = $("#cmFormForm input[name='task_id']").val();
		task.form = [];
		removeObjFromArray(processForm,"task_id",task.task_id);
		$("#cmFormForm").find("tbody tr[editable='false']").each(function(i,e){
			inputOne = {};
			inputOne.id = "formObj" + (i+1);
			inputOne.name = $(this).find("input:text").val();
			inputOne.type = $(this).find("select").val();
			inputOne.isRequire = $(this).find("input:checkbox").is(":checked")
			task.form.push(inputOne);
		});
		processForm.push(task);
		console.log(JSON.stringify(processForm));
		$("#cmFormModal").modal("hide");
	});
	/**————————————————————————————————————转出条件————————————————————————————————————***/
	var setJudge = function(taskId, gatewayId){
		var from_name = _canvas.getBlockById(taskId).task_name;
		var conns = _canvas.getConnBySource(gatewayId);//从网关出的线
		if(conns){
			var tr = ""
			var old_conns = getJudgeBySourceId(_canvas.getBlockById(taskId).task_id);
			if(old_conns){
				for(var i in old_conns){
					tr+="<tr data-connid='" + old_conns[i].connection_id +"' ><td><span>" + old_conns[i].name + "</span></td><td>";
					var $option = $("<select name='formobj' class='form-control'></select>");
					var startpointForm = getFormById($("#flowdesign_canvas div[task_type='startpoint']").attr("id")); 
					if(startpointForm)
					for(var j in startpointForm.form){
						$option.append("<option value='"+startpointForm.form[j].id+"' "+ (old_conns[i].formobj==startpointForm.form[j].id?"selected='selected'":"") +">" + startpointForm.form[j].name +"</option>");
					}
					tr+= $option.prop("outerHTML") + "</td>";
					var $logic = $("<select name='logic' class='form-control'>" +
					 "<option value='=='>等于</option>"+
					 "<option value='>'>大于</option>"+
					 "<option value='<'>小于</option>"+
					 "<option value='>='>大于等于</option>"+
					 "<option value='<='>小于等于</option>"+
				     "</select>");
					$logic.find("option[value='"+old_conns[i].logic+"']").attr("selected","selected");
					tr+= "<td>"+ $logic.prop("outerHTML") +"</td>";
					tr+= "<td><input type='text' class='form-control' value='"+old_conns[i].logic_value+"'/></td>";
					tr+= "</tr>";
				}
			}
			
			for(var i in conns){
				var flag = false;
				for(var j in old_conns){
					if(conns[i].connection_id == old_conns[j].connection_id){
						flag = true;
						break;
					}
				}
				if(!flag){
						var to_name = _canvas.getBlockById(conns[i].to_id).task_name;
						tr+= "<tr data-connid='"+ conns[i].connection_id +"' ><td><span>"+from_name+"→"+to_name+"</span></td><td>";
						var $option = $("<select name='formobj' class='form-control'></select>");
						var startpointForm = getFormById($("#flowdesign_canvas div[task_type='startpoint']").attr("id")); 
						if(startpointForm)
						for(var j in startpointForm.form){
							$option.append("<option value='"+startpointForm.form[j].id+"'>" + startpointForm.form[j].name +"</option>");
						}
						tr+= $option.prop("outerHTML") + "</td>";
						tr+= "<td><select name='logic' class='form-control'>" +
							 "<option value='=='>等于</option>"+
							 "<option value='>'>大于</option>"+
							 "<option value='<'>小于</option>"+
							 "<option value='>='>大于等于</option>"+
							 "<option value='<='>小于等于</option>"+
						     "</select></td>";
						tr+= "<td><input type='text' class='form-control' /></td>";
						tr+= "</tr>";
				}
			}
			
			
			$("#cmJudgeForm table tbody").html(tr);
			return true;
		}else{
			return false;
		}
	}
	
	/**确定*/
	$(document).on("click","#submitCmJudge",function(e){
		$("#cmJudgeForm tbody tr").each(function(i,e){
			var conn = _canvas.getConnById($(this).data("connid"));
			var source_task_id = $("#cmJudgeForm input[name='task_id']").val();
			removeObjFromArray(processJudge,"source_task_id",source_task_id);
			processJudge.push({
				connection_id : conn.connection_id,
				source_task_id : source_task_id,
				from_id: conn.from_id,
				to_id : conn.to_id,
				name : $(this).find("td:eq(0) span").text(),
				formobj: $(this).find("td:eq(1) select[name='formobj']").val(),
				logic: $(this).find("td:eq(2) select[name='logic']").val(),
				logic_value:$(this).find("td:eq(3) input:text").val()
			});
		});
		console.log(JSON.stringify(processJudge));
		$("#cmJudgeModal").modal("hide");
	});
	
	var getFormById = function(id){
		if(processForm){
			for(var i in processForm){
				if(processForm[i].task_id == id) return processForm[i];
			}
		}
	}
	
	var getJudgeBySourceId = function(sourceId){
		var conn = [];
		if(processJudge){
			for(var i in processJudge){
				if(processJudge[i].source_task_id == sourceId){
					conn.push(processJudge[i]);
				}
			}
			return conn;
		}
	}
	/**————————————————————————————————————导出结构————————————————————————————————————***/
	//导出流程图的json结构
	$(document).on("click","#btn-export",function(e){
		var workflowObj = _canvas.exportJson();
		bootbox.alert(JSON.stringify(workflowObj));
	});
	/**————————————————————————————————————保存设计————————————————————————————————————***/
	//保存按钮
	$(document).on("click","#btn-save",function(e){
		if(window.processData.process_name){
			$("#btnSaveForm input[name='process_name']").val(window.processData.process_name);
		}
		$("#btnSaveModal").modal('show');
	});
	//确定按钮
	$(document).on("click","#submitBtnSave",function(e){
		var workflowObj = _canvas.exportJson();
		window.processData = workflowObj;
		window.processData.process_name = $("#btnSaveForm input[name='process_name']").val();
		
		$.post(CTX+"/designer/saveDesign",{
			"processData":JSON.stringify(window.processData),
			"processForm":JSON.stringify(window.processForm),
			"processJudge":JSON.stringify(window.processJudge)
		},function(r){
			$("#btnSaveModal").modal('hide');
			bootbox.alert("保存成功！");
		})
	});
	/**————————————————————————————————————载入流程————————————————————————————————————***/
	//载入流程
	$(document).on("click","#btn-load",function(e){
		$.post(CTX+"/designer/findProcessDefinations",null,function(r){
			if(r.success){
				$("#loadProcessForm select[name='process_defination_id']").html('');
				for(var i in r.data){
					$("#loadProcessForm select[name='process_defination_id']").append("<option value='"+r.data[i].id+"'>" + r.data[i].processDefinationName + "</option>");
				}
			}
		});
		$("#loadProcessModal").modal("show");
	});
	//确定按钮
	$(document).on("click","#submitLoadProcess",function(e){
		if(confirm("你确定清空画板吗?")){
			_canvas.refresh();
			$("#flowdesign_canvas div").each(function(i,e){
				if($(this).hasClass("process-step"))
				_canvas.delProcess($(this).attr("process_id"));
			});
			window.processData = {};
			window.processForm = [];
			window.processJudge = [];
			$.post(CTX+"/designer/findProcessDefinationById",{
				"id":$("#loadProcessForm select[name='process_defination_id']").val()
			},function(r){
				if(r.success && r.data){
					window.processData = eval("("+r.data.processData+")");
					window.processForm = eval("("+r.data.processForm+")");
					window.processJudge = eval("("+r.data.processJudge+")");
					$("#btnSaveForm input[name='process_name']").val(window.processData.process_name);
					flowDesignOptions.processData = window.processData;
					_canvas = $("#flowdesign_canvas").Flowdesign(flowDesignOptions);
					
					//因为新载入的流程，线的id会重新生成，所以需要把新id替换旧id
					var workflowObj = _canvas.exportJson();
					$.each(workflowObj.connections,function(i){
						for(var j=0;j<window.processJudge.length;j++){
							if(workflowObj.connections[i].from_id == window.processJudge[j].from_id
								&& workflowObj.connections[i].to_id == window.processJudge[j].to_id){
								window.processJudge[j].connection_id = workflowObj.connections[i].connection_id;
							}
						}
					});
					
					$("#loadProcessModal").modal('hide');
					bootbox.alert("载入成功！");
				}
			});
		}
		
	});
});


var removeObjFromArray = function(array,variable,task_id){
	for(var i in array){
		if(array[i][variable] == task_id) array.splice(i, 1);
	}
}

var findObjFromArray = function(array,task_id){
	for(var i in array){
		if(array[i].task_id == task_id) return array[i].form;
	}
}



/**得到新id*/
var maxBlockId = function(type){
	var ids =  [];
	$("#flowdesign_canvas div").each(function (idx, elem) {
	    var $elem = $(elem);
	    if($elem.hasClass('process-step')){
	    	ids.push(parseInt($elem.attr("process_id")));
	    }
	});
	if(ids.length>0){
		return Math.max.apply(null, ids)+1;
	}else{
		return 1;
	}
}



