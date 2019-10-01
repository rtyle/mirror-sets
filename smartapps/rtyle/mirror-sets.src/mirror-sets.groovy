// vim: ts=4:sw=4
/**
 *	Mirror Sets
 *
 *	Copyright 2019 Ross Tyler
 *
 *	Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *	in compliance with the License. You may obtain a copy of the License at:
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *	on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *	for the specific language governing permissions and limitations under the License.
 */
definition(
	name			: 'Mirror Sets',
	namespace		: 'rtyle',
	author			: 'Ross Tyler',
	description		: 'Switch and level changes from a device in the primary set are forwarded to all others. Changes from a device in the secondary set are forwarded only to those in the primary set',
	category		: 'Convenience',
	singleInstance	: false,
	iconUrl			: 'https://raw.githubusercontent.com/rtyle/mirror-sets/master/smartapps/rtyle/mirror-sets.src/app.png',
	iconX2Url		: 'https://raw.githubusercontent.com/rtyle/mirror-sets/master/smartapps/rtyle/mirror-sets.src/app@2x.png',
	iconX3Url		: 'https://raw.githubusercontent.com/rtyle/mirror-sets/master/smartapps/rtyle/mirror-sets.src/app@3x.png',
)

preferences {
	section('Title') {
		input 'primary', 'capability.Switch', title: 'Primary', multiple: true
		input 'secondary', 'capability.Switch', title: 'Secondary', multiple: true
	}
}

// return the peers of a device
private List<physicalgraph.app.DeviceWrapper> peers(physicalgraph.app.DeviceWrapper device) {
	// a physicalgraph.app.DeviceWrapper instance may not compare equal to that in a list
	// even though they have the same deviceNetworkId.
	// instead, compare explicitly using deviceNetworkId.
	String deviceNetworkId = device.deviceNetworkId
	if (primary.find {deviceNetworkId == it.deviceNetworkId}) {
		// everything in the primary and secondary sets except us
		(primary + secondary).findAll {deviceNetworkId != it.deviceNetworkId} 
	} else {
		// everything in the primary set except us
		primary.findAll {deviceNetworkId != it.deviceNetworkId}
	}
}

void respond(physicalgraph.app.DeviceWrapper from, Closure forward) {
	// forward from this device to its peers
	// unless this device was recently forwarded from somewhere else.
	// this will prevent a recently forwarded event from triggering us again.
	long now = now()
	if (4000 < now - state.time[from.deviceNetworkId]) {
		peers(from).each {physicalgraph.app.DeviceWrapper to ->
			state.time[to.deviceNetworkId] = now
			forward(to)
		}
	}
}

void respondToSwitch(physicalgraph.app.EventWrapper e) {
	respond e.device, {
		it."$e.value"()
	}
}

void respondToLevel(physicalgraph.app.EventWrapper e) {
	respond e.device, {
		it.setLevel e.value
	}
}

private void initialize() {
	state.time = [:]
	(primary + secondary).each {physicalgraph.app.DeviceWrapper it ->
		state.time[it.deviceNetworkId] = 0
		subscribe it, 'switch'	, respondToSwitch
		subscribe it, 'level'	, respondToLevel
	}
}

def installed() {
	initialize()
}

def updated() {
	unsubscribe()
	initialize()
}
