package com.automationrockstars.bmo;

import java.util.Map;

public class ContextStoryReporter implements StoryReporter {

    @Override
    public String name() {
        return "CONTEXT";
    }

    @Override
    public void start() {
        Context.forName(Context.GENERAL);
    }

    @Override
    public void finish() {
        Context.forName(Context.GENERAL).clear();
    }

    @Override
    public void beforeStory(String name, String description, String path) {

    }

    @Override
    public void afterStory() {
        Context.forName(Context.STORY).clear();
    }

    @Override
    public void beforeScenario(String scenarioTitle) {

    }

    @Override
    public void afterScenario() {
        Context.forName(Context.SCENARIO).clear();
    }

    @Override
    public void example(Map<String, String> tableRow) {

    }

    @Override
    public void beforeStep(String step) {

    }

    private void closeStepContext(){
        Context.forName(Context.STEP).clear();
    }
    @Override
    public void successful(String step) {
        closeStepContext();
    }

    @Override
    public void ignorable(String step) {
        closeStepContext();
    }

    @Override
    public void pending(String step) {

    }

    @Override
    public void notPerformed(String step) {

    }

    @Override
    public void failed(String step, Throwable cause) {
        closeStepContext();
    }

    @Override
    public void attach(byte[] attachment, String title, String mimeType) {

    }
}
