package com.hyp.util;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TableBlock;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.renderer.html.AttributeProviderContext;
import org.commonmark.renderer.html.AttributeProviderFactory;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.*;

public class MarkdownUtils {
    /**
     * 基础应用
     * @param markdown
     * @return
     */
    public static String markdownToHtml(String markdown){
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }
    /**
     * 增加拓展应用
     * @param markdown
     * @return
     */
    public static String markdownToHtmlExtensions(String markdown){
//        标题生成id
        Set<Extension> headAnchorExtensions = Collections.singleton(HeadingAnchorExtension.create());
//        转换table的html
        List<Extension> tableExtension = Arrays.asList(TablesExtension.create());
        Parser parser = Parser.builder()
                .extensions(tableExtension)
                .build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder()
                .extensions(headAnchorExtensions)
                .extensions(tableExtension)
                .attributeProviderFactory(new AttributeProviderFactory() {
                    public AttributeProvider create(AttributeProviderContext context) {
                        return new CustomAttributeProcider();
                    }
                })
                .build();
        return renderer.render(document);
    }
    /**
     * 处理标签属性
     */
    static class CustomAttributeProcider implements AttributeProvider {
        @Override
        public void setAttributes(Node node, String tagName, Map<String,String> attributes){
//            改a标签属性target 为 _blank
            if(node instanceof Link){
                attributes.put("target","_blank");
            }
            if(node instanceof TableBlock){
                attributes.put("class","ui celled table");
            }
        }
    }
}
