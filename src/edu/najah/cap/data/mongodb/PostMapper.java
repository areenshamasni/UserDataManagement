package edu.najah.cap.data.mongodb;

import edu.najah.cap.posts.Post;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostMapper implements IDocMapper<Post> {
    private static final Logger logger = LoggerFactory.getLogger(PostMapper.class);

    @Override
    public Document mapToDocument(Post post) {
        Document document = new Document();
        try {
            document.append("title", post.getTitle())
                    .append("body", post.getBody())
                    .append("Author", post.getAuthor())
                    .append("postDate", post.getDate());
            logger.info("Posts mapped to Document");
        } catch (Exception e) {
            logger.error("error in mapping user posts to document");
        }
        return document;
    }
}
