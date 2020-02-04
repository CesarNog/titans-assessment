package com.swisscom.humanresources

import spock.lang.Specification

class SpockSpec extends Specification {
    void 'should compare equal'() {
        expect:
        true == true
    }
}
