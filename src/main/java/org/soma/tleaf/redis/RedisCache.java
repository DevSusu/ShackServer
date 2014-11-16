package org.soma.tleaf.redis;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soma.tleaf.accesskey.AccessKey;
import org.soma.tleaf.accesskey.AccessKeyManager;
import org.soma.tleaf.exception.DatabaseConnectionException;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import redis.clients.jedis.Jedis;

import com.google.gson.Gson;

/**
 * Customized actual Cache that We will use.
 * @author susu
 * Date : Nov 12, 2014 12:32:37 PM
 */
public class RedisCache implements Cache {

	private String name;
	private int expireSeconds;

	@Inject
	private AccessKeyManager accessKeyManager;

	private Jedis jedis;

	private static final Logger logger = LoggerFactory.getLogger(RedisCache.class);
	private static Gson gson = new Gson();

	private static final int VAILD_FOR_SEC = 300; // 5 min
	private static final String CACHE_NAME = "accesskey";

	public RedisCache ( Jedis jedis ) {
		this ( CACHE_NAME , jedis , VAILD_FOR_SEC );
	}

	public RedisCache ( String name, Jedis jedis, int expireSeconds ) {

		this.name = name;
		this.expireSeconds = expireSeconds;
		this.jedis = jedis;

	}

	public boolean isAccessKeyValid ( String accessKey, String appId, String userId ) {

		AccessKey accessKey_server = (AccessKey) get ( accessKey ).get();

		// if accessKey was not found
		if( accessKey_server == null ) {
			return false;
		}

		return accessKey_server.isValid(accessKey, appId, userId);

	}

	/**
	 * Deletes AccessKey in the Cache
	 * @author susu
	 * Date Nov 12, 2014
	 * @param key String
	 */
	@Override
	public void evict(Object key) {
		
		jedis.del( (String)key );
		logger.info("Successfully deleted AccessKey");

	}
	public void delete( String key ) {
		evict ( key );
	}

	/**
	 * Gets AccessKey with String key
	 * @author susu
	 * Date Nov 12, 2014
	 * @param key String id
	 * @return ValueWrapper If AccessKey wasn't found in both Redis and Couch ( or Failed to Connect to DB ), return null
	 */
	@Override
	public ValueWrapper get(Object key) {

		logger.info("retriving Value by Key...");

		String accessKeyString; AccessKey accessKey = null;
			
		accessKeyString = jedis.get( (String)key );
		
		if ( accessKeyString == null ) { 

			logger.info("There was no AccessKey matching the Key in Redis. Searching in CouchDB...");

			try {
				accessKey = accessKeyManager.findAccessKey( (String) key );
			} catch (DatabaseConnectionException e) {
				e.printStackTrace();
				return null;
			}
			if ( accessKey == null ) return null;

			// Put it back again in redis.
			put ( accessKey.getAccessKey() , accessKey );
		}
		else {

			logger.info("Found Key in Redis. Mapping.");
			accessKey = gson.fromJson(accessKeyString, AccessKey.class);
		}
		
		return new SimpleValueWrapper( accessKey );
	}

	/**
	 * Save AccesKey into Redis Cache
	 * @author susu
	 * Date Nov 12, 2014
	 * @param key String id
	 * @param value AccessKey Object
	 */
	@Override
	public void put(Object key, Object value) {

		logger.info("Inserting Value...");

		if ( !(key instanceof String) || !(value instanceof AccessKey) ) {
			logger.info("Parameters are not Appropriate");
			return ;
		}

		String sKey = (String)key;
		AccessKey accessKey = (AccessKey)value;
		String accessKeyString = gson.toJson(accessKey);
		
		jedis.set(sKey, accessKeyString);
		jedis.expire(sKey, expireSeconds);

	}

	/**
	 * Deletes all data in the Cache
	 * @author susu
	 * Date Nov 12, 2014
	 */
	@Override
	public void clear() {
		jedis.flushAll();
	}


	@Override
	public String getName() {
		return name;
	}



	@Override
	public Object getNativeCache() {
		return jedis;
	}

}