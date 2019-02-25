package com.dvt.ActivitiWorkflow.commons.query;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.QueryTranslatorImpl;
import org.hibernate.hql.spi.QueryTranslator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.dvt.ActivitiWorkflow.commons.entity.BaseBean;
import com.dvt.ActivitiWorkflow.commons.entity.BaseEntity;
import com.google.common.collect.Lists;




/**
 * 动态jpql/nativesql查询的实现类
 * 
 * @author lij
 * @lastModified lij 2014-9-11 10:26:22
 */
@Repository
public class DynamicQueryImpl implements DynamicQuery {

	@PersistenceContext
	private EntityManager em;

	/**
	 * 从EntityManager获取Hibernate的Session
	 * 下面所有方式的实现都基于Hibernate
	 */
	private Session getHibernateSession() { 
		return em.unwrap(Session.class);
	}
	
	/**
	 * 获取Hibernate的SessionFactory对象
	 * @return
	 */
	private SessionFactory getHibernateSessionFactory() {
		return getHibernateSession().getSessionFactory();
	}
	
	// -====================================== jpql查询 ======================================-

	@Override
	public List<?> query(String jpql, Object... params) {
		return createQuery(jpql, params).getResultList();
	}
	
	@Override
	public <T> T querySingleResult(Class<T> resultClass, String jpql, Object...params) {
		return createTypedQuery(resultClass, jpql, params).getSingleResult();
	}
	
	@Override
	public <T> List<T> query(Class<T> resultClass, String jpql, Object... params) {
		return createTypedQuery(resultClass, jpql, params).getResultList();
	}

	@Override
	public List<?> queryPagingList(Pageable pageable, String jpql, Object... params) {
		Integer pageNumber = pageable.getPageNumber();
		Integer pageSize = pageable.getPageSize();
		Integer startPosition = pageNumber * pageSize;
		return createQuery(jpql, params).setFirstResult(startPosition).setMaxResults(pageSize).getResultList();
	}
	
	@Override
	public <T> List<T> queryPagingList(Class<T> resultClass, Pageable pageable, String jpql, Object... params) {
		Integer pageNumber = pageable.getPageNumber();
		Integer pageSize = pageable.getPageSize();
		Integer startPosition = pageNumber * pageSize;
		return createTypedQuery(resultClass, jpql, params).setFirstResult(startPosition).setMaxResults(pageSize).getResultList();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Page query(Pageable pageable, String jpql, Object... params) {
		List<?> rows = queryPagingList(pageable, jpql, params);
		Long total = queryCount(jpql, params);
		return new PageImpl(rows, pageable, total);
	}
	
	@Override
	public <T> Page<T> query(Class<T> resultClass, Pageable pageable, String jpql, Object... params) {
		List<T> rows = queryPagingList(resultClass, pageable, jpql, params);
		Long total = queryCount(jpql, params);
		return new PageImpl<T>(rows, pageable, total);
	}
	
	@Override
	public Long queryCount(String jpql, Object... params) {
		jpql = StringUtils.substringBefore(jpql, "order by"); // 去掉order by, 提升执行效率
		
		// 去重和分组只能使用NativeSQL统计查询
		if (jpql.contains("distinct") || jpql.contains("group by")) {
			String countSql = generateCountSql(jpql);
			Object count = createNativeQuery(countSql, params).getSingleResult();
			return ((Number) count).longValue();
		} else { // 使用jpql统计查询
			String countJpql = generateCountJpql(jpql);
			return (Long) createQuery(countJpql, params).getSingleResult();
		}
	}
	
	@Override
	public int executeUpdate(String jpql, Object... params) {
		return createQuery(jpql, params).executeUpdate();
	}
	
	
	
	// -====================================== 本地查询 ======================================-
	
	private <T> List<T> transformModelObject(Class<T> resultClass, List cacheList){
		List<T> resultList = Lists.newArrayList();
		if(CollectionUtils.isNotEmpty(cacheList)){
				for (Object obj : cacheList) {
					try {
						T row = resultClass.newInstance();
						List<Method> methods = Arrays.asList(row.getClass().getMethods());
						methods.sort((Method e1,Method e2) -> e1.getName().compareTo(e2.getName()));//按方法字母顺序排序
						
						Object[] columns = (Object[])obj;
						for (Object col : columns) {//遍历结果集一行的每列
							if(col!=null && !"null".equals(col.toString()))
							for (Method method : methods) {
								System.out.println(method.getName());
								if(method.getName().startsWith("set") &&
										method.getParameterTypes()[0].getName().equals(col.getClass().getName())){
									if(col.getClass().getName().equals("java.lang.String")){
										method.invoke(row, col.toString());
										methods.remove(method);
										break;
									}
									if(col.getClass().getName().equals("java.lang.Integer")){
										method.invoke(row, (Integer)col);
										methods.remove(method);
										break;
									}
									if(col.getClass().getName().equals("int")){
										method.invoke(row, (int)col);
										methods.remove(method);
										break;
									}
									if(col.getClass().getName().equals("java.lang.Long")){
										method.invoke(row, (Long)col);
										methods.remove(method);
										break;
									}
									if(col.getClass().getName().equals("long")){
										method.invoke(row, (long)col);
										methods.remove(method);
										break;
									}
									if(col.getClass().getName().equals("java.lang.Double")){
										method.invoke(row, (Double)col);
										methods.remove(method);
										break;
									}
									if(col.getClass().getName().equals("double")){
										method.invoke(row, (double)col);
										methods.remove(method);
										break;
									}
									if(col.getClass().getName().equals("java.lang.Float")){
										method.invoke(row, (Float)col);
										methods.remove(method);
										break;
									}
									if(col.getClass().getName().equals("float")){
										method.invoke(row, (float)col);
										methods.remove(method);
										break;
									}
									if(col.getClass().getName().equals("java.lang.Blooean")){
										method.invoke(row, (Boolean)col);
										methods.remove(method);
										break;
									}
									if(col.getClass().getName().equals("boolean")){
										method.invoke(row, (boolean)col);
										methods.remove(method);
										break;
									}
									if(col.getClass().getName().equals("java.sql.Timestamp")){
										method.invoke(row, (Timestamp)col);
										methods.remove(method);
										break;
									}
									if(col.getClass().getName().equals("java.util.Date")){
										method.invoke(row, (Date)col);
										methods.remove(method);
										break;
									}
								}
							}
							
						}
						resultList.add(row);
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
		}
		return resultList;
	}
	
	private <T> T transformModelObject(Class<T> resultClass, Object cache){
		
		if(cache!=null){
					try {
						T row = resultClass.newInstance();
						List<Method> methods = Arrays.asList(row.getClass().getMethods());
						methods.sort((Method e1,Method e2) -> e1.getName().compareTo(e2.getName()));//按方法字母顺序排序
						
						Object[] columns = (Object[])cache;
						for (Object col : columns) {//遍历结果集一行的每列
							if(col!=null && !"null".equals(col.toString()))
							for (Method method : methods) {
								if(method.getName().startsWith("set") &&
										method.getParameterTypes()[0].getName().equals(col.getClass().getName())){
									if(col.getClass().getName().equals("java.lang.String")){
										method.invoke(row, col.toString());
										methods.remove(method);
										break;
									}
									if(col.getClass().getName().equals("java.lang.Integer")){
										method.invoke(row, (Integer)col);
										methods.remove(method);
										break;
									}
									if(col.getClass().getName().equals("int")){
										method.invoke(row, (int)col);
										methods.remove(method);
										break;
									}
									if(col.getClass().getName().equals("java.lang.Long")){
										method.invoke(row, (Long)col);
										methods.remove(method);
										break;
									}
									if(col.getClass().getName().equals("long")){
										method.invoke(row, (long)col);
										methods.remove(method);
										break;
									}
									if(col.getClass().getName().equals("java.lang.Double")){
										method.invoke(row, (Double)col);
										methods.remove(method);
										break;
									}
									if(col.getClass().getName().equals("double")){
										method.invoke(row, (double)col);
										methods.remove(method);
										break;
									}
									if(col.getClass().getName().equals("java.lang.Float")){
										method.invoke(row, (Float)col);
										methods.remove(method);
										break;
									}
									if(col.getClass().getName().equals("float")){
										method.invoke(row, (float)col);
										methods.remove(method);
										break;
									}
									if(col.getClass().getName().equals("java.lang.Blooean")){
										method.invoke(row, (Boolean)col);
										methods.remove(method);
										break;
									}
									if(col.getClass().getName().equals("boolean")){
										method.invoke(row, (boolean)col);
										methods.remove(method);
										break;
									}
									if(col.getClass().getName().equals("java.sql.Timestamp")){
										method.invoke(row, (Timestamp)col);
										methods.remove(method);
										break;
									}
									if(col.getClass().getName().equals("java.util.Date")){
										method.invoke(row, (Date)col);
										methods.remove(method);
										break;
									}
								}
							}
							
						}
						return row;
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
		}
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> nativeQuery(String nativeSql, Object... params) {
		return createNativeQuery(nativeSql, params).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> nativeQuery(Class<T> resultClass, String nativeSql, Object... params) {
		List cacheList =  getNativeQuery(resultClass, nativeSql, params).getResultList();
		if(isBean(resultClass)){
			return transformModelObject(resultClass, cacheList); 
		}
		return cacheList;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T nativeQuerySingleResult(Class<T> resultClass, String nativeSql, Object... params) {
		Object cacheObject =  getNativeQuery(resultClass, nativeSql, params).getSingleResult();
		if(isBean(resultClass)){
			return transformModelObject(resultClass, cacheObject); 
		}
		return (T)cacheObject;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> nativeQueryPagingList(Pageable pageable, String nativeSql, Object... params) {
		Integer pageNumber = pageable.getPageNumber();
		Integer pageSize = pageable.getPageSize();
		Integer startPosition = pageNumber * pageSize;
		return createNativeQuery(nativeSql, params).setFirstResult(startPosition).setMaxResults(pageSize).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> nativeQueryPagingList(Class<T> resultClass, Pageable pageable, String nativeSql, Object... params) {
		Integer pageNumber = pageable.getPageNumber();
		Integer pageSize = pageable.getPageSize();
		Integer startPosition = pageNumber * pageSize;
		List cacheList = getNativeQuery(resultClass, nativeSql, params).setFirstResult(startPosition).setMaxResults(pageSize).getResultList();
		if(isBean(resultClass)){
			return transformModelObject(resultClass, cacheList); 
		}
		return cacheList;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Page nativeQuery(Pageable pageable, String nativeSql, Object... params) {
		List<Object[]> rows = nativeQueryPagingList(pageable, nativeSql, params);
		Long total = nativeQueryCount(nativeSql, params);
		return new PageImpl(rows, pageable, total);
	}
	
	@Override
	public <T> Page<T> nativeQuery(Class<T> resultClass, Pageable pageable, String nativeSql, Object... params) {
		List<T> rows = nativeQueryPagingList(resultClass, pageable, nativeSql, params);
		Long total = nativeQueryCount(nativeSql, params);
		return new PageImpl<T>(rows, pageable, total);
	}
	
	@Override
	public Long nativeQueryCount(String nativeSql, Object... params) {
		nativeSql = StringUtils.substringBefore(nativeSql, "order by"); // 去掉order by, 提升执行效率
		String countSql = "select count(*) from (" + nativeSql + ") _count";
		Object count = createNativeQuery(countSql, params).getSingleResult();
		return ((Number) count).longValue();
	}
	
	@Override
	public int nativeExecuteUpdate(String nativeSql, Object... params) {
		return createNativeQuery(nativeSql, params).executeUpdate();
	}
	
	// -=================================== 其它 ===========================================-
	
	private Query createQuery(String jpql, Object... params) {
		Query q = em.createQuery(jpql);
		if(params!=null)
		for (int i = 0; i < params.length; i++) {
			q.setParameter(i + 1, params[i]); // 与Hiberante不同,jpa query从位置1开始
		}
		return q;
	}
	
	private <T> TypedQuery<T> createTypedQuery(Class<T> resultClass, String jpql, Object... params) {
		TypedQuery<T> q = em.createQuery(jpql, resultClass);
		if(params!=null)
		for (int i = 0; i < params.length; i++) {
			q.setParameter(i + 1, params[i]); // 与Hiberante不同,jpa query从位置1开始
		}
		return q;
	}
	
	private <T> Query getNativeQuery(Class<T> resultClass, String sql, Object... params) {
		Query q = null;
		
		if(isEntity(resultClass)){
			q = createNativeQuery(resultClass, sql, params);
		} else{
			q = createNativeQuery(sql, params);
		}
		return q;
	}
	
	private <T> Boolean isEntity(Class<T> resultClass) {
		return BaseEntity.class.isAssignableFrom(resultClass);
	}
	
	private <T> Boolean isBean(Class<T> resultClass) {
		return BaseBean.class.isAssignableFrom(resultClass);
	}
	
	private Query createNativeQuery(String sql, Object... params) {
		Query q = em.createNativeQuery(sql);
		if(params!=null)
		for (int i = 0; i < params.length; i++) {
			q.setParameter(i + 1, params[i]); // 与Hiberante不同,jpa query从位置1开始
		}
		return q;
	}
	
	
	private <T> Query createNativeQuery(Class<T> resultClass, String sql, Object... params) {
		Query q = em.createNativeQuery(sql, resultClass);
		if(params!=null)
		for (int i = 0; i < params.length; i++) {
			q.setParameter(i + 1, params[i]); // 与Hiberante不同,jpa query从位置1开始
		}
		return q;
	}
	
	/**
	 * 执行统计查询
	 * @param jpql
	 * @param params 命名参数
	 * @return
	 */
	private String generateCountJpql(String jpql) {
		return "select count(*) from " + StringUtils.substringAfter(jpql, "from");
	}

	/**
	 * 通过jpql生成统计sql
	 * @param jpql
	 * @return
	 */
	private String generateCountSql(String jpql) {
		return "select count(*) c from (" + jpqlToSql(jpql) + ") _count";
	}

	/**
	 * 通过hibernate的翻译器(QueryTranslator)将jpql翻译成sql
	 * @param jpql
	 * @return
	 */
	private String jpqlToSql(String jpql) {
		QueryTranslator qt = new QueryTranslatorImpl(jpql, jpql,
				Collections.EMPTY_MAP,
				(SessionFactoryImplementor) getHibernateSessionFactory());
		qt.compile(Collections.EMPTY_MAP, false);
		return qt.getSQLString();
	}

}
