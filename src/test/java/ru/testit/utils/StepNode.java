package ru.testit.utils;

import lombok.Getter;
import lombok.Setter;
import ru.testit.model.request.Attachment;
import ru.testit.services.*;
import java.util.*;

public class StepNode
{
    private StepNode parent;
    private String title;
    private String description;
    private Date startedOn;
    private Date completedOn;
    private String outcome;
    private Throwable failureReason;
    private List<LinkItem> linkItems/* = new ArrayList<>()*/;
    private List<StepNode> childrens/* = new ArrayList<>()*/;
    @Setter @Getter
    private Map<String, String> parameters;
    @Getter
    private final List<Attachment> attachments = new ArrayList<>();

    public StepNode() {
        this.linkItems = new LinkedList<LinkItem>();
        this.childrens = new LinkedList<StepNode>();
    }

    public StepNode getParent() {
        return this.parent;
    }
    
    public void setParent(final StepNode parent) {
        this.parent = parent;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public List<StepNode> getChildrens() {
        return this.childrens;
    }
    
    public void setChildrens(final List<StepNode> childrens) {
        this.childrens = childrens;
    }
    
    public String getOutcome() {
        return this.outcome;
    }
    
    public void setOutcome(final String outcome) {
        this.outcome = outcome;
    }
    
    public Date getStartedOn() {
        return this.startedOn;
    }
    
    public void setStartedOn(final Date startedOn) {
        this.startedOn = startedOn;
    }
    
    public Date getCompletedOn() {
        return this.completedOn;
    }
    
    public void setCompletedOn(final Date completedOn) {
        this.completedOn = completedOn;
    }
    
    public Throwable getFailureReason() {
        return this.failureReason;
    }
    
    public void setFailureReason(final Throwable failureReason) {
        this.failureReason = failureReason;
    }
    
    public List<LinkItem> getLinkItems() {
        return this.linkItems;
    }

    public void addLinkItem(LinkItem item) {
        this.linkItems.add(item);
    }
    
    public void setLinkItems(final List<LinkItem> linkItems) {
        this.linkItems = linkItems;
    }
}
