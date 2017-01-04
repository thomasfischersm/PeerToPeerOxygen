package com.playposse.peertopeeroxygen.android;

import com.playposse.peertopeeroxygen.android.missiondependencies.MissionCycleDetectorTest;
import com.playposse.peertopeeroxygen.android.missiondependencies.MissionLadderSorterTest;
import com.playposse.peertopeeroxygen.android.ui.widgets.missiontree.MissionTreeBuilderTest;
import com.playposse.peertopeeroxygen.android.ui.widgets.missiontree.MissionWrapperTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * A test suite that runs all the pure unit tests.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        MissionCycleDetectorTest.class,
        MissionLadderSorterTest.class,
        MissionTreeBuilderTest.class,
        MissionWrapperTest.class,
})
public class UnitTestSuite {
}
