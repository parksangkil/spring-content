package internal.org.springframework.content.autoconfigure.solr;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.FIt;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.JustBeforeEach;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.apache.solr.client.solrj.SolrClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;
import org.springframework.content.commons.annotations.MimeType;
import org.springframework.content.commons.operations.ContentOperations;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jRunner;

@RunWith(Ginkgo4jRunner.class)
public class SolrUpdateEventHandlerTests {

	private SolrUpdateEventHandler handler;
	
	// mocks
	private SolrClient solrClient;
	private ContentOperations ops;
	
	// args 
	private Object contentEntity;
	private Exception e;
	
	{
		Describe("SolrUpdateEventHandler", () -> {
			BeforeEach(() -> {
				solrClient = mock(SolrClient.class);
				ops = mock(ContentOperations.class);
				handler = new SolrUpdateEventHandler(solrClient, ops);
			});
			Context("#onAfterSetContent", () -> {
				JustBeforeEach(() -> {
					try {
						handler.onAfterSetContent(contentEntity);
					} catch (Exception e) {
						this.e = e;
					}
				});
				Context("given a content entity", () -> {
					BeforeEach(() -> {
						contentEntity = new ContentEntity();
						((ContentEntity)contentEntity).contentId = UUID.randomUUID().toString();
						((ContentEntity)contentEntity).contentLen = 128L;
						((ContentEntity)contentEntity).mimeType = "text/plain";
					});
					It("should call update", () -> {
						assertThat(e, is(nullValue()));
						verify(solrClient).request(anyObject(), anyString());
					});
				});
				Context("given a content entity with a null contentId", () -> {
					BeforeEach(() -> {
						contentEntity = new ContentEntity();
					});
					It("should call update", () -> {
						assertThat(e, is(nullValue()));
						verify(solrClient, never()).request(anyObject(), anyString());
					});
				});
				Context("given a bogus content entity", () -> {
					BeforeEach(() -> {
						contentEntity = new NotAContentEntity();
					});
					It("", ()->{
						assertThat(e, is(nullValue()));
						verify(solrClient, never()).request(anyObject(), anyString());
					});
				});
			});
			Context("#onBeforeUnsetContent", () -> {
				JustBeforeEach(() -> {
					try {
						handler.onBeforeUnsetContent(contentEntity);
					} catch (Exception e) {
						this.e = e;
					}
				});
				Context("given a content entity", () -> {
					BeforeEach(() -> {
						contentEntity = new ContentEntity();
						((ContentEntity)contentEntity).contentId = UUID.randomUUID().toString();
						((ContentEntity)contentEntity).contentLen = 128L;
						((ContentEntity)contentEntity).mimeType = "text/plain";
					});
					It("should call deleteById", () -> {
						assertThat(e, is(nullValue()));
						verify(solrClient).deleteById(((ContentEntity)contentEntity).contentId.toString());
						verify(solrClient).commit();
					});
				});
				Context("given a content entity with a null contentId", () -> {
					BeforeEach(() -> {
						contentEntity = new ContentEntity();
					});
					It("should call update", () -> {
						assertThat(e, is(nullValue()));
						verify(solrClient, never()).deleteById(anyString());
					});
				});
				Context("given a bogus content entity", () -> {
					BeforeEach(() -> {
						contentEntity = new NotAContentEntity();
					});
					It("", ()->{
						assertThat(e, is(nullValue()));
						verify(solrClient, never()).deleteById(anyString());
					});
				});
			});
		});
	}

	public static class ContentEntity {
		@ContentId public String contentId;
		@ContentLength public Long contentLen;
		@MimeType public String mimeType;
	}
	
	public static class NotAContentEntity {
	}
	
	@Test
	public void test() {
	}

}