/**
 * Copyright  2006  V Narayan Raman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
_sahi.sid = '$sessionId';
try{
top._sahi.sid = '$sessionId';
top._sahi.isWinOpen = $isWindowOpen;
top._sahi.createCookie('sahisid', '$sessionId');
top._sahi._isPaused = $isSahiPaused;
top._sahi._isPlaying = $isSahiPlaying;
top._sahi._isRecording = $isSahiRecording;
_sahi.hotKey = '$hotkey';

INTERVAL = $interval;
ONERROR_INTERVAL = $onErrorInterval;
MAX_RETRIES = $maxRetries;
SAHI_MAX_WAIT_FOR_LOAD = $maxWaitForLoad;

_sahi.waitForLoad = SAHI_MAX_WAIT_FOR_LOAD;
interval = INTERVAL;

var waitCondn = "$waitCondition";
if (!String.isBlankOrNull(waitCondn) && waitCondn != "null"){
    top._sahi.waitCondition = waitCondn;
}
var time = "$waitConditionTime";
if (!String.isBlankOrNull(time) && time != "-1"){
    var diff = time - new Date().valueOf();
    top._sahi.waitInterval = (diff > 0) ? diff : -1;
}
}catch(e){}
