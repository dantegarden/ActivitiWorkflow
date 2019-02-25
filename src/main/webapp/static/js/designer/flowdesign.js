/**
 * 流程设计器 js工具类
 * @author lij
 * **/

String.prototype.startWith=function(str){     
  var reg=new RegExp("^"+str);     
  return reg.test(this);        
};  

(function($) {
	var defaults = {
		      processData:{},//步骤节点数据
		      //processUrl:'',//步骤节点数据
		      fnRepeat:function(){
		        alert("步骤连接重复");
		      },
		      fnClick:function(){
		        alert("单击");
		      },
		      fnDbClick:function(){
		        alert("双击");
		      },
		      canvasMenus : {
		        "one": function(t) {alert('画面右键')}
		      },
		      processMenus: {
		        "one": function(t) {alert('步骤右键')}
		      },
		      /*右键菜单样式*/
		      menuStyle: {
		        border: '1px solid #5a6377',
		        minWidth:'150px',
		        padding:'5px 0'
		      },
		      itemStyle: {
		        fontFamily : 'verdana',
		        color: '#333',
		        border: '0',
		        /*borderLeft:'5px solid #fff',*/
		        padding:'5px 40px 5px 20px'
		      },
		      itemHoverStyle: {
		        border: '0',
		        /*borderLeft:'5px solid #49afcd',*/
		        color: '#fff',
		        backgroundColor: '#5a6377'
		      },
		      mtAfterDrop:function(params){
		          //alert('连接成功后调用');
		          //alert("连接："+params.sourceId +" -> "+ params.targetId);
		      },
		      //这是连接线路的绘画样式
		      connectorPaintStyle : {
		          lineWidth:3,
		          strokeStyle:"#49afcd",
		          joinstyle:"round"
		      },
		      //鼠标经过样式
		      connectorHoverStyle : {
		          lineWidth:3,
		          strokeStyle:"#da4f49"
		      }

		   };/*defaults end*/
	
	 var initEndPoints = function(){
	      $(".process-flag").each(function(i,e) {
	          var p = $(e).parent();
	          jsPlumb.makeSource($(e), {
	              parent:p,
	              anchor:"Continuous",
	              endpoint:[ "Dot", { radius:1 } ],
	              connector:[ "Flowchart", { stub:[5, 5] } ],
	              connectorStyle:defaults.connectorPaintStyle,
	              hoverPaintStyle:defaults.connectorHoverStyle,
	              dragOptions:{},
	              maxConnections:-1
	          });
	      });
	 }
	 
	 
	 var updateProcessTo =function(obj){
		 if(obj.blocks){
			 $.each(obj.blocks,function(i,block){
				 if(block.process_to){$("#"+block.task_id).attr("process_to", block.process_to);}
				 else{$("#"+block.task_id).attr("process_to", "");}
			});
		 }
	 }
	 
	 /*设置隐藏域保存关系信息*/
	  var aConnections = [];
	  var setConnections = function(conn, remove) {
	      if (!remove) aConnections.push(conn);
	      else {
	          var idx = -1;
	          for (var i = 0; i < aConnections.length; i++) {
	              if (aConnections[i] == conn) {
	                  idx = i; break;
	              }
	          }
	          if (idx != -1) aConnections.splice(idx, 1);
	      }
	      if (aConnections.length > 0) {
	          var s = "";
	          for ( var j = 0; j < aConnections.length; j++ ) {
	              var from = $('#'+aConnections[j].sourceId).attr('process_id');
	              var target = $('#'+aConnections[j].targetId).attr('process_id');
	              s = s + "<input type='hidden' value=\"" + from + "," + target + "\">";
	          }
	          $('#my_process_info').html(s);
	      } else {
	          $('#my_process_info').html('');
	      }
	      jsPlumb.repaintEverything();//重画
	  };
	  
	  $.fn.Flowdesign = function(options){
		  var _canvas = $(this);
		  //右键步骤的步骤号
	      _canvas.append('<input type="hidden" id="my_active_id" value="0"/>');
	      _canvas.append('<input type="hidden" id="my_copy_id" value="0"/>');
	      _canvas.append('<div id="my_process_info"></div>');
	      /*配置*/
	      $.each(options, function(i, val) {
	          if (typeof val == 'object' && defaults[i])
	        	$.extend(defaults[i], val);
	          else 
	            defaults[i] = val;
	      });
	      /*画布右键绑定*/
	      var contextmenu = {
	          bindings: defaults.canvasMenus,
	          menuStyle : defaults.menuStyle,
	          itemStyle : defaults.itemStyle,
	          itemHoverStyle : defaults.itemHoverStyle
	      }
	      $(this).contextMenu('canvasMenu',contextmenu);
	      jsPlumb.importDefaults({
	            DragOptions : { cursor: 'pointer'},
	            EndpointStyle : { fillStyle:'#225588' },
	            Endpoint : [ "Dot", {radius:1} ],
	            ConnectionOverlays : [
	                [ "Arrow", { location:1 } ],
	                [ "Label", {
	                        location:0.1,
	                        id:"label",
	                        cssClass:"aLabel"
	                    }]
	            ],
	            Anchor : 'Continuous',
	            ConnectorZIndex:5,
	            HoverPaintStyle:defaults.connectorHoverStyle
	      });
	      if(!$.support.leadingWhitespace){ //ie9以下，用VML画图
	            jsPlumb.setRenderMode(jsPlumb.VML);
	      } else { //其他浏览器用SVG
	            jsPlumb.setRenderMode(jsPlumb.SVG);
	      }
	      //初始化原步骤
	      var lastProcessId=0;
	      var processData = options.processData;
	      if(processData.blocks){
	    	  $.each(processData.blocks, function(i,row) {
	    		  var nodeDiv = document.createElement('div');
	              var nodeId = row.task_id
	              var badge = 'badge-inverse'
	              if(nodeId.startWith("task")){//任务节点
	    	              var icon = 'fa fa-male';
	    	              if(row.task_type&&row.task_type=="startpoint"){//第一步
	    	                badge = 'badge-info';
	    	                icon = 'fa fa-pencil';
	    	              }
	    	              if(row.task_type){
	    	            	if(row.task_type=="startpoint"){ icon = "fa fa-pencil"; }
	    	            	else if(row.task_type=="single-user"){ icon = "fa fa-user"; }
	    	            	else if(row.task_type=="single-role"){ icon = "fa fa-comment"; }
	    	            	else if(row.task_type=="mult-user"){ icon = "fa fa-group"; }
	    	            	else if(row.task_type=="mult-role"){ icon = "fa fa-comments"; }
	    	            	else if(row.task_type=="assign-one"){ icon = "fa fa-male"; }
	    	              }
	    	              $(nodeDiv).attr("id",nodeId)
	    			                .attr("style",row.style)
	    			                .attr("task_type",row.task_type)
	    			                .attr("process_to",row.process_to)
	    			                .attr("process_id",row.process_id)
	    			                .attr("process_role",row.process_role)
	    			                .addClass("process-step btn btn-small processtask")
	    	              .html('<span class="process-flag badge '+badge+'"><i class="'+icon+' icon-white"></i></span>' + row.task_name )
	    	              .mousedown(function(e){
	    	                if( e.which == 3 ) { //右键绑定
	    	                    _canvas.find('#my_active_id').val(row.process_id);
	    	                    contextmenu.bindings = defaults.processMenus;
	    	                    $("#processMenu li").show();
	    	                    $(this).contextMenu('processMenu', contextmenu);
	    	                }
	    	              });  
	              }else if(nodeId.startWith("gateway")){
	            	  var icon = 'fa fa-random';
	            	  $(nodeDiv).attr("id",nodeId)
		                .attr("style",row.style)
		                .attr("process_to",row.process_to)
		                .attr("process_id",row.process_id)
		                .addClass("diamond process-step btn btn-small processtask")
		                .html('<span class="process-flag badge '+badge+'"><i class="'+icon+' icon-white"></i></span>' + row.task_name )
		                .mousedown(function(e){
  			  			  if( e.which == 3 ) { //右键绑定
  			  				  _canvas.find('#my_active_id').val(row.process_id);
  			  				  contextmenu.bindings = defaults.processMenus;
  			  				  $("#processMenu li:lt(4)").hide();
  			  				  $(this).contextMenu('processMenu', contextmenu);
  			  			  }
  			  		  });
	              }
	              
	              _canvas.append(nodeDiv);
	              //索引变量
	              lastProcessId = row.process_id;
	    	  });//each
	      }
	      var timeout = null;
	      //点击或双击事件,这里进行了一个单击事件延迟，因为同时绑定了双击事件
	      $(".process-step").on('click',function(){
	          //激活
	          _canvas.find('#my_active_id').val($(this).attr("process_id")),
	          clearTimeout(timeout);
	          var obj = this;
	          timeout = setTimeout(defaults.fnClick,300);
	      }).on('dblclick',function(){
	          clearTimeout(timeout);
	          defaults.fnDbClick();
	      });
	      //使之可拖动
	      jsPlumb.draggable(jsPlumb.getSelector(".process-step"));
	      initEndPoints();
	      //绑定添加连接操作。画线-input text值  拒绝重复连接
	      jsPlumb.bind("jsPlumbConnection", function(info) {
	          setConnections(info.connection);
	          updateProcessTo(Flowdesign.exportJson());
	      });
	      //绑定删除connection事件
	      jsPlumb.bind("jsPlumbConnectionDetached", function(info) {
	          setConnections(info.connection, true);
	          updateProcessTo(Flowdesign.exportJson());
	      });
	      //绑定删除确认操作
	      jsPlumb.bind("click", function(c) {
	        if(confirm("你确定取消连接吗?"))
	          jsPlumb.detach(c);
	          removeObjFromArray(window.processJudge,"connection_id",c.id);
	      });
	      //连接成功回调函数
	      function mtAfterDrop(params)
	      {
	          //console.log(params)
	          defaults.mtAfterDrop({sourceId:$("#"+params.sourceId).attr('process_id'),targetId:$("#"+params.targetId).attr('process_id')});
	      }
	      jsPlumb.makeTarget(jsPlumb.getSelector(".process-step"), {
	          dropOptions:{ hoverClass:"hover", activeClass:"active" },
	          anchor:"Continuous",
	          maxConnections:-1,
	          endpoint:[ "Dot", { radius:1 } ],
	          paintStyle:{ fillStyle:"#ec912a",radius:1 },
	          hoverPaintStyle:this.connectorHoverStyle,
	          beforeDrop:function(params){
	              if(params.sourceId == params.targetId) return false;/*不能链接自己*/
	              var j = 0;
	              $('#my_process_info').find('input').each(function(i){
	                  var str = $('#' + params.sourceId).attr('process_id') + ',' + $('#' + params.targetId).attr('process_id');
	                  if(str == $(this).val()){
	                      j++;
	                      return;
	                  }
	              })
	              if( j > 0 ){
	                  defaults.fnRepeat();
	                  return false;
	              } else {
	                  mtAfterDrop(params);
	                  return true;
	              }
	          }
	      });
	      //reset  start
	      var _canvas_design = function(){

	          //连接关联的步骤
	    	  if(processData && processData.connections)
	    	  $.each(processData.connections,function(index){
	    		  var id = processData.connections[index].connection_id;
	    		  var sourceId = processData.connections[index].from_id;
	    		  var targetId = processData.connections[index].to_id;
	    		  jsPlumb.connect({
                      source:sourceId, 
                      target:targetId,
                      id:id
                     /* ,labelStyle : { cssClass:"component label" } */
                     /* label : label */
                  });
                  return ;
	    	  });
	         
	      }//_canvas_design end reset 
	      _canvas_design();
	      
	      //-----外部调用----------------------
	      var Flowdesign = {
	    		  addProcess:function(row){
		    			  if(row.id<=0){
		    				  return false;
		    			  }
		    			  
	    			  	  if(row.block_type=="task"){
	    			  		  //任务节点
	    			  		  var nodeDiv = document.createElement('div');
	    			  		  var nodeId = "task" + row.id;
	    			  		  var badge = 'badge-inverse';
	    			  		  var icon = 'fa fa-male';
	    			  		  var style = "position: relative;left:" + row.left + ";top:" + row.top+";"
	    			  		  if(row.task_type){
	    			  			  if(row.task_type=="startpoint"){ icon = "fa fa-pencil"; }
	    			  			  else if(row.task_type=="single-user"){ icon = "fa fa-user"; }
	    			  			  else if(row.task_type=="single-role"){ icon = "fa fa-comment"; }
	    			  			  else if(row.task_type=="mult-user"){ icon = "fa fa-group"; }
	    			  			  else if(row.task_type=="mult-role"){ icon = "fa fa-comments"; }
	    			  			  else if(row.task_type=="assign-one"){ icon = "fa fa-male"; }
	    			  		  }
	    			  		  $(nodeDiv).attr("id",nodeId)
	    			  		  //.attr("style",style)
	    			  		  .attr("task_type",row.task_type)
	    			  		  .attr("process_id",row.id)
	    			  		  .addClass("process-step btn btn-small processtask")
	    			  		  .html('<span class="process-flag badge '+badge+'"><i class="'+icon+' icon-white"></i></span>' + row.task_name )
	    			  		  .mousedown(function(e){
	    			  			  if( e.which == 3 ) { //右键绑定
	    			  				  _canvas.find('#my_active_id').val(row.id);
	    			  				  contextmenu.bindings = defaults.processMenus
	    			  				  $(this).contextMenu('processMenu', contextmenu);
	    			  			  }
	    			  		  });
	    			  		  _canvas.append(nodeDiv);
	    			  		  //使之可拖动 和 连线
	    			  		  jsPlumb.draggable(jsPlumb.getSelector(".process-step"));
	    			  		  initEndPoints();
	    			  		  //使可以连接线
	    			  		  jsPlumb.makeTarget(jsPlumb.getSelector(".process-step"), {
	    			  			  dropOptions:{ hoverClass:"hover", activeClass:"active" },
	    			  			  anchor:"Continuous",
	    			  			  maxConnections:-1,
	    			  			  endpoint:[ "Dot", { radius:1 } ],
	    			  			  paintStyle:{ fillStyle:"#ec912a",radius:1 },
	    			  			  hoverPaintStyle:this.connectorHoverStyle,
	    			  			  beforeDrop:function(params){
	    			  				  var j = 0;
	    			  				  $('#my_process_info').find('input').each(function(i){
	    			  					  var str = $('#' + params.sourceId).attr('process_id') + ',' + $('#' + params.targetId).attr('process_id');
	    			  					  if(str == $(this).val()){
	    			  						  j++;
	    			  						  return;
	    			  					  }
	    			  				  })
	    			  				  if( j > 0 ){
	    			  					  defaults.fnRepeat();
	    			  					  return false;
	    			  				  } else {
	    			  					  return true;
	    			  				  }
	    			  			  }
	    			  		  });
	    			  	  }else if(row.block_type=="gateway"){//网关节点
	    			  		  var nodeDiv = document.createElement('div');
	    			  		  var nodeId = "gateway" + row.id;
	    			  		  var badge = 'badge-inverse';
	    			  		  var icon = 'fa fa-random';
	    			  		  var style = "position: relative;left:" + row.left + ";top:" + row.top+";"
	    			  		  $(nodeDiv).attr("id",nodeId)
	    			  		  //.attr("style",style)
	    			  		  .attr("process_id",row.id)
	    			  		  .addClass("diamond process-step btn btn-small processtask")
	    			  		  .html('<span class="process-flag badge '+badge+'"><i class="'+icon+' icon-white"></i></span>' + row.task_name )
	    			  		  .mousedown(function(e){
	    			  			  if( e.which == 3 ) { //右键绑定
	    			  				  _canvas.find('#my_active_id').val(row.id);
	    			  				  contextmenu.bindings = defaults.processMenus
	    			  				  $(this).contextMenu('processMenu', contextmenu);
	    			  			  }
	    			  		  });
	    			  		  _canvas.append(nodeDiv);
	    			  		  //使之可拖动 和 连线
	    			  		  jsPlumb.draggable(jsPlumb.getSelector(".process-step"));
	    			  		  initEndPoints();
	    			  		  //使可以连接线
	    			  		  jsPlumb.makeTarget(jsPlumb.getSelector(".process-step"), {
	    			  			  dropOptions:{ hoverClass:"hover", activeClass:"active" },
	    			  			  anchor:"Continuous",
	    			  			  maxConnections:-1,
	    			  			  endpoint:[ "Dot", { radius:1 } ],
	    			  			  paintStyle:{ fillStyle:"#ec912a",radius:1 },
	    			  			  hoverPaintStyle:this.connectorHoverStyle,
	    			  			  beforeDrop:function(params){
	    			  				  var j = 0;
	    			  				  $('#my_process_info').find('input').each(function(i){
	    			  					  var str = $('#' + params.sourceId).attr('process_id') + ',' + $('#' + params.targetId).attr('process_id');
	    			  					  if(str == $(this).val()){
	    			  						  j++;
	    			  						  return;
	    			  					  }
	    			  				  })
	    			  				  if( j > 0 ){
	    			  					  defaults.fnRepeat();
	    			  					  return false;
	    			  				  } else {
	    			  					  return true;
	    			  				  }
	    			  			  }
	    			  		  });
	    			  	  }
	    			  	  return true;
	    		  },
	    		  delProcess:function(activeId){
	    	            if(activeId<=0) return false;
	    	            
	    	            $("#task"+activeId).remove();
	    	            $("#gateway"+activeId).remove();
	    	            return true;
	    	      },
	    	      getActiveId:function(){
	    	          return _canvas.find("#my_active_id").val();
	    	      },
	    	      copy:function(active_id){
	    	          if(!active_id)
	    	            active_id = _canvas.find("#my_active_id").val();

	    	          _canvas.find("#my_copy_id").val(active_id);
	    	          return true;
	    	      },
	    	      paste:function(){
	    	          return  _canvas.find("#my_copy_id").val();
	    	      },
	    	      getBlockById:function(id){
	    	    	  var blocks = {}
  	    		 	  $("#flowdesign_canvas div[id='"+id+"']").each(function (idx, elem) {
  	    		 	    var $elem = $(elem);
  	    		 	    blocks = {
  	    		 	        task_id: $elem.attr('id'),
  	    		 	        task_name: $elem.text(),
  	    		 	        task_type: $elem.attr('task_type'),
  	    		 	        process_id: $elem.attr('process_id'),
  	    		 	        process_role:$elem.attr('process_role'),
  	    		 	        style: "left:" + parseInt($elem.css("left"), 10) +"px;top:" + parseInt($elem.css("top"), 10) +"px;",
  	    		 	    }
  	    		 	  });
	    	    	  return blocks;
	    	      },
	    	      getConnById:function(id){
	    	    	  var conn = {};
  	    		 	  $.each(jsPlumb.getConnections(), function (idx, connection) {
  	    		 		if(connection.id == id)
  	    		 			conn = {
  	    		 	        connection_id: connection.id,
  	    		 	        from_id: connection.sourceId,
  	    		 	        to_id: connection.targetId
  	    		 	    };
  	    		 	  });
  	    		 	  return conn;
	    	      },
	    	      getConnBySource:function(sourceId){
	    	    	  /**按节点源获得线*/
	    	    	  var connections = [];
  	    		 	  $.each(jsPlumb.getConnections(), function (idx, connection) {
  	    		 		if(connection.sourceId == sourceId)
  	    		 	    connections.push({
  	    		 	        connection_id: connection.id,
  	    		 	        from_id: connection.sourceId,
  	    		 	        to_id: connection.targetId
  	    		 	    });
  	    		 	  });
  	    		 	  return connections;
	    	      },
	    	      exportJson:function(){
	    	    	  /**导出结构*/
	    	    		 	var tasknum = 0;
	    	    		 	var blocks = []
	    	    		 	$("#flowdesign_canvas div[id^='task']").each(function (idx, elem) {
	    	    		 	    var $elem = $(elem);
	    	    		 	    blocks.push({
	    	    		 	        task_id: $elem.attr('id'),
	    	    		 	        task_name: $elem.text(),
	    	    		 	        task_type: $elem.attr('task_type'),
	    	    		 	        process_id: $elem.attr('process_id'),
	    	    		 	        process_role:$elem.attr('process_role'),
	    	    		 	        style: "left:" + parseInt($elem.css("left"), 10) +"px;top:" + parseInt($elem.css("top"), 10) +"px;",
	    	    		 	        
	    	    		 	    });
	    	    		 	    tasknum++;
	    	    		 	});
	    	    		 	$("#flowdesign_canvas div[id^='gateway']").each(function (idx, elem) {
	    	    		 	    var $elem = $(elem);
	    	    		 	    blocks.push({
	    	    		 	        task_id: $elem.attr('id'),
	    	    		 	        task_name: $elem.text(),
	    	    		 	        process_id: $elem.attr('process_id'),
	    	    		 	        style: "left:" + parseInt($elem.css("left"), 10) +"px;top:" + parseInt($elem.css("top"), 10) +"px;",
	    	    		 	    });
	    	    		 	});
	    	    		 	var connections = [];
	    	    		 	$.each(jsPlumb.getConnections(), function (idx, connection) {
	    	    		 	    connections.push({
	    	    		 	        connection_id: connection.id,
	    	    		 	        from_id: connection.sourceId,
	    	    		 	        to_id: connection.targetId
	    	    		 	    });
	    	    		 	});
	    	    		 	
	    	    		 	for(var i=0;i<blocks.length;i++){
	    	    		 		for(var j=0;j<connections.length;j++){
	    	    		 			if(connections[j].from_id == blocks[i].task_id){
	    	    		 				var to_id = '';
	    	    		 				if(connections[j].to_id.startWith("task")){
	    	    		 					to_id = connections[j].to_id.replace('task','');
	    	    		 				}else if(connections[j].to_id.startWith("gateway")){
	    	    		 					to_id = connections[j].to_id.replace('gateway','');
	    	    		 				}
	    	    		 				
	    	    		 				if(blocks[i].process_to){
	    	    		 					blocks[i].process_to += ","+ to_id;
	    	    		 				} else{
	    	    		 					blocks[i].process_to = to_id;
	    	    		 				}
	    	    		 			}
	    	    		 		}
	    	    		 	}
	    	    		 	
	    	    		 	var workflowObj = {}
	    	    		 	workflowObj.total = tasknum;
	    	    		 	workflowObj.blocks = blocks;
	    	    		 	workflowObj.connections = connections;
	    	    		 	return workflowObj;
	    	     },
	    	     clear:function(){
	    	          try{
	    	                jsPlumb.detachEveryConnection();
	    	                jsPlumb.deleteEveryEndpoint();
	    	                $('#my_process_info').html('');
	    	                jsPlumb.repaintEverything();
	    	                return true;
	    	            }catch(e){
	    	                return false;
	    	          }
	    	     },
	    	     refresh:function(){
	    	          try{
	    	                //jsPlumb.reset();
	    	                this.clear();
	    	                //_canvas_design();
	    	                return true;
	    	            }catch(e){
	    	                return false;
	    	          }
	    	     }
	      };
	      return Flowdesign;
	  }
})(jQuery);