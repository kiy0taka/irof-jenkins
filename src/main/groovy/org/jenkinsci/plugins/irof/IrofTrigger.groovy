package org.jenkinsci.plugins.irof

import hudson.model.BuildableItem
import hudson.model.Cause
import hudson.model.Item
import hudson.Extension
import hudson.triggers.Trigger
import hudson.triggers.TriggerDescriptor
import twitter4j.Status

import org.kohsuke.stapler.DataBoundConstructor

class IrofTrigger extends Trigger<BuildableItem> {

    @DataBoundConstructor
    public IrofTrigger() {}

    void run(Status status) {
        job.scheduleBuild(0, new IrofTriggerCause(status))
    }

    @Extension
    public static class DescriptorImpl extends TriggerDescriptor {

        public boolean isApplicable(Item item) {
            item in BuildableItem
        }

        public String getDisplayName() {
            'いろふ'
        }
    }

    public static class IrofTriggerCause extends Cause {

        Status status

        public IrofTriggerCause(Status status) {
            this.status = status
        }

        @Override
        public String getShortDescription() {
            'Started by irof'
        }

        @Override
        public boolean equals(Object o) {
            o in IrofTriggerCause
        }

        @Override
        public int hashCode() {
            1602
        }
    }

}