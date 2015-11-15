import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class TestMongo {
	
	private Vertx vertx;
	private MongoClient client;
	
	@Before
	public void setup() {
		vertx = Vertx.vertx();
		client = MongoClient.createShared(vertx, new JsonObject());
	}
	
	
	@Test
	public void insertMany(TestContext context) {
		AtomicInteger counter = new AtomicInteger();
		Async async = context.async();
		while (counter.getAndIncrement() < 10) {
			int i = counter.intValue();
			client.insert("test-collection", createTestObject(i), res -> {
				if (res.failed()) {
					context.fail(res.cause());
				}
				String s = res.result();
				System.out.println(s);
				if (res.succeeded() && i == 10) {
					async.complete();
				}
			});
		}
	}
	
	private static JsonObject createTestObject(int i) {
		JsonObject json = new JsonObject();
		json.put("test", "something");
		json.put("int", i);
		json.put("somefield", "somevalue");
		json.put("someDate", new Date().toString());
		return json;
	}
	
	@After
	public void tearDown(TestContext context) {
		if (vertx != null) {
			vertx.close(context.asyncAssertSuccess());
		}
	}
}
