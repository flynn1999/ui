package org.iplantc.de.analysis.client.views;

import static org.iplantc.de.client.models.analysis.AnalysisExecutionStatus.*;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.common.collect.Lists;
import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;

@RunWith(GxtMockitoTestRunner.class)
public class AnalysesViewMenuImplTest {


    @Mock IplantDisplayStrings mockDisplayStrings;
    @Mock IplantResources mockResources;

    @Mock SelectionChangedEvent<Analysis> mockSelectionEvent;

    @Mock MenuItem goToFolderMiMock;
    @Mock MenuItem viewParamsMiMock;
    @Mock MenuItem relaunchMock;
    @Mock MenuItem cancelMiMock;
    @Mock MenuItem deleteMiMock;
    @Mock MenuItem renameMiMock;
    @Mock MenuItem updateCommentsMiMock;

    private AnalysesViewMenuImpl uut;

    @Before public void setUp() {
        uut = new AnalysesViewMenuImpl(mockDisplayStrings, mockResources);
        mockMenuItems(uut);
    }
    void mockMenuItems(AnalysesViewMenuImpl uut){
        uut.goToFolderMI = goToFolderMiMock;
        uut.viewParamsMI = viewParamsMiMock;
        uut.relaunchMI = relaunchMock;
        uut.cancelMI = cancelMiMock;
        uut.deleteMI = deleteMiMock;
        uut.renameMI = renameMiMock;
        uut.updateCommentsMI = updateCommentsMiMock;
    }

    @Test public void testOnSelectionChanged_ZeroSelected() {
        when(mockSelectionEvent.getSelection()).thenReturn(Collections.<Analysis>emptyList());
        uut.onSelectionChanged(mockSelectionEvent);

        verify(goToFolderMiMock).setEnabled(eq(false));
        verify(viewParamsMiMock).setEnabled(eq(false));
        verify(relaunchMock).setEnabled(eq(false));
        verify(cancelMiMock).setEnabled(eq(false));
        verify(deleteMiMock).setEnabled(eq(false));
        verify(renameMiMock).setEnabled(eq(false));
        verify(updateCommentsMiMock).setEnabled(eq(false));
    }



    @Test public void testOnSelectionChanged_OneSelected_appEnabled() {
        uut = new AnalysesViewMenuImpl(mockDisplayStrings, mockResources){
            @Override
            boolean canCancelSelection(final List<Analysis> selection){
                return true;
            }

            @Override
            boolean canDeleteSelection(List<Analysis> selection) {
                return true;
            }
        };
        mockMenuItems(uut);
        final Analysis mockAnalysis = mock(Analysis.class);
        // Selected analysis' app is Enabled
        when(mockAnalysis.isAppDisabled()).thenReturn(false);
        when(mockSelectionEvent.getSelection()).thenReturn(Lists.newArrayList(mockAnalysis));
        uut.onSelectionChanged(mockSelectionEvent);

        verify(goToFolderMiMock).setEnabled(eq(true));
        verify(viewParamsMiMock).setEnabled(eq(true));
        verify(relaunchMock).setEnabled(eq(true));
        verify(cancelMiMock).setEnabled(eq(true));
        verify(deleteMiMock).setEnabled(eq(true));
        verify(renameMiMock).setEnabled(eq(true));
        verify(updateCommentsMiMock).setEnabled(eq(true));
    }

    @Test public void testOnSelectionChanged_OneSelected_appDisabled() {
        uut = new AnalysesViewMenuImpl(mockDisplayStrings, mockResources){
            @Override
            boolean canCancelSelection(final List<Analysis> selection){
                return true;
            }

            @Override
            boolean canDeleteSelection(List<Analysis> selection) {
                return true;
            }
        };
        mockMenuItems(uut);
        final Analysis mockAnalysis = mock(Analysis.class);
        // Selected analysis' app is disabled
        when(mockAnalysis.isAppDisabled()).thenReturn(true);
        when(mockSelectionEvent.getSelection()).thenReturn(Lists.newArrayList(mockAnalysis));
        uut.onSelectionChanged(mockSelectionEvent);

        verify(goToFolderMiMock).setEnabled(eq(true));
        verify(viewParamsMiMock).setEnabled(eq(true));
        verify(relaunchMock).setEnabled(eq(false));
        verify(cancelMiMock).setEnabled(eq(true));
        verify(deleteMiMock).setEnabled(eq(true));
        verify(renameMiMock).setEnabled(eq(true));
        verify(updateCommentsMiMock).setEnabled(eq(true));
    }

    @Test public void testOnSelectionChanged_ManySelected() {
        uut = new AnalysesViewMenuImpl(mockDisplayStrings, mockResources){
            @Override
            boolean canCancelSelection(final List<Analysis> selection){
                return true;
            }

            @Override
            boolean canDeleteSelection(List<Analysis> selection) {
                return true;
            }
        };
        mockMenuItems(uut);
        final Analysis mockAnalysis = mock(Analysis.class);
        // Selected analysis' app is Enabled
        when(mockAnalysis.isAppDisabled()).thenReturn(false);
        when(mockSelectionEvent.getSelection()).thenReturn(Lists.newArrayList(mockAnalysis, mock(Analysis.class)));
        uut.onSelectionChanged(mockSelectionEvent);

        verify(goToFolderMiMock).setEnabled(eq(false));
        verify(viewParamsMiMock).setEnabled(eq(false));
        verify(relaunchMock).setEnabled(eq(false));
        verify(cancelMiMock).setEnabled(eq(true));
        verify(deleteMiMock).setEnabled(eq(true));
        verify(renameMiMock).setEnabled(eq(false));
        verify(updateCommentsMiMock).setEnabled(eq(false));
    }

    @Test public void testCanCancelSelection() {
        Analysis mock1 = mock(Analysis.class);

        when(mock1.getStatus()).thenReturn(SUBMITTED.toString());
        assertTrue("Selection should be cancellable", uut.canCancelSelection(Lists.newArrayList(mock1)));

        when(mock1.getStatus()).thenReturn(RUNNING.toString());
        assertTrue("Selection should be cancellable", uut.canCancelSelection(Lists.newArrayList(mock1)));

        when(mock1.getStatus()).thenReturn(IDLE.toString());
        assertTrue("Selection should be cancellable", uut.canCancelSelection(Lists.newArrayList(mock1)));

        when(mock1.getStatus()).thenReturn(UNKNOWN.toString());
        assertFalse("Selection should be not be cancellable", uut.canCancelSelection(Lists.newArrayList(mock1)));

        when(mock1.getStatus()).thenReturn(COMPLETED.toString());
        assertFalse("Selection should be not be cancellable", uut.canCancelSelection(Lists.newArrayList(mock1)));

        when(mock1.getStatus()).thenReturn(HELD.toString());
        assertFalse("Selection should be not be cancellable", uut.canCancelSelection(Lists.newArrayList(mock1)));

        when(mock1.getStatus()).thenReturn(FAILED.toString());
        assertFalse("Selection should be not be cancellable", uut.canCancelSelection(Lists.newArrayList(mock1)));

        when(mock1.getStatus()).thenReturn(SUBMISSION_ERR.toString());
        assertFalse("Selection should be not be cancellable", uut.canCancelSelection(Lists.newArrayList(mock1)));

        when(mock1.getStatus()).thenReturn(REMOVED.toString());
        assertFalse("Selection should be not be cancellable", uut.canCancelSelection(Lists.newArrayList(mock1)));
    }

    @Test public void testCanDeleteSelection() {
        Analysis mock1 = mock(Analysis.class);
        Analysis mock2 = mock(Analysis.class);
        Analysis mock3 = mock(Analysis.class);

        when(mock1.getStatus()).thenReturn(SUBMITTED.toString());
        when(mock2.getStatus()).thenReturn(RUNNING.toString());
        assertFalse("Selection should not be deletable", uut.canDeleteSelection(Lists.newArrayList(mock1, mock2, mock3)));

        when(mock1.getStatus()).thenReturn(RUNNING.toString());
        when(mock2.getStatus()).thenReturn(COMPLETED.toString());
        assertFalse("Selection should not be deletable", uut.canDeleteSelection(Lists.newArrayList(mock3, mock1, mock2)));

        when(mock1.getStatus()).thenReturn(COMPLETED.toString());
        when(mock2.getStatus()).thenReturn(RUNNING.toString());
        assertFalse("Selection should not be deletable", uut.canDeleteSelection(Lists.newArrayList(mock1, mock2, mock3)));

        when(mock1.getStatus()).thenReturn(COMPLETED.toString());
        when(mock2.getStatus()).thenReturn(FAILED.toString());
        when(mock3.getStatus()).thenReturn(RUNNING.toString());
        assertFalse("Selection should not be deletable", uut.canDeleteSelection(Lists.newArrayList(mock1, mock2, mock3)));

        when(mock1.getStatus()).thenReturn(COMPLETED.toString());
        when(mock2.getStatus()).thenReturn(FAILED.toString());
        when(mock3.getStatus()).thenReturn(COMPLETED.toString());
        assertTrue("Selection should be deletable", uut.canDeleteSelection(Lists.newArrayList(mock1, mock2, mock3)));
    }
}