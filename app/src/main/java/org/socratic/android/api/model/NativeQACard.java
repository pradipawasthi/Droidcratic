package org.socratic.android.api.model;

public class NativeQACard {

    private Body body;
    private Content content;
    private Header header;
    private String url;

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static class Body {
        private String bg_color;

        public String getBg_color() {
            return bg_color;
        }

        public void setBg_color(String bg_color) {
            this.bg_color = bg_color;
        }
    }

    public static class Content {
        private String answer;
        private String answer_html;
        private String answer_markdown;
        private String answer_text;
        private String host;
        private String logo_color;
        private String logo_img;
        private String name;
        private String question;
        private String question_description;
        private String question_markdown;
        private double score;
        private String url;
        private QuestionHighlights question_highlights;

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public String getAnswer_html() {
            return answer_html;
        }

        public void setAnswer_html(String answer_html) {
            this.answer_html = answer_html;
        }

        public String getAnswer_markdown() {
            return answer_markdown;
        }

        public void setAnswer_markdown(String answer_markdown) {
            this.answer_markdown = answer_markdown;
        }

        public String getAnswer_text() {
            return answer_text;
        }

        public void setAnswer_text(String answer_text) {
            this.answer_text = answer_text;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getLogo_color() {
            return logo_color;
        }

        public void setLogo_color(String logo_color) {
            this.logo_color = logo_color;
        }

        public String getLogo_img() {
            return logo_img;
        }

        public void setLogo_img(String logo_img) {
            this.logo_img = logo_img;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getQuestion_description() {
            return question_description;
        }

        public void setQuestion_description(String question_description) {
            this.question_description = question_description;
        }

        public String getQuestion_markdown() {
            return question_markdown;
        }

        public void setQuestion_markdown(String question_markdown) {
            this.question_markdown = question_markdown;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public QuestionHighlights getQuestion_highlights() {
            return question_highlights;
        }

        public void setQuestion_highlights(QuestionHighlights question_highlights) {
            this.question_highlights = question_highlights;
        }
    }

    public static class Header {
        private String bg_color;
        private String icon;
        private String text;
        private String text_color;

        public String getBg_color() {
            return bg_color;
        }

        public void setBg_color(String bg_color) {
            this.bg_color = bg_color;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getText_color() {
            return text_color;
        }

        public void setText_color(String text_color) {
            this.text_color = text_color;
        }
    }
}