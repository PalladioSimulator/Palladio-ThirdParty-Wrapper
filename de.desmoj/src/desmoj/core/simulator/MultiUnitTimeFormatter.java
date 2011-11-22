package desmoj.core.simulator;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Use this class to format TimeSpan and TimeInstant objects like in the
 * following examples: 59 Minutes, 28 Seconds, and 548 Milliseconds could be formatted like
 * O:59:28:548 or 59:28 or 59:28:548:000:000 or 0.59.28.
 * 
 * @version DESMO-J, Ver. 2.3.3 copyright (c) 2011
 * @author Felix Klueckmann
 * 
 *         Licensed under the Apache License, Version 2.0 (the "License"); you
 *         may not use this file except in compliance with the License. You may
 *         obtain a copy of the License at
 *         http://www.apache.org/licenses/LICENSE-2.0
 * 
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *         implied. See the License for the specific language governing
 *         permissions and limitations under the License.
 * 
 */
public class MultiUnitTimeFormatter implements TimeFormatter {

	private EnumSet<TimeUnit> _unitSet;
	private char _separator;
	private static Map<TimeUnit, Integer> numberOfDigits = new EnumMap<TimeUnit, Integer>(
			TimeUnit.class);

	static {
		numberOfDigits.put(TimeUnit.HOURS, 2);
		numberOfDigits.put(TimeUnit.MINUTES, 2);
		numberOfDigits.put(TimeUnit.SECONDS, 2);
		numberOfDigits.put(TimeUnit.MILLISECONDS, 3);
		numberOfDigits.put(TimeUnit.MICROSECONDS, 3);
		numberOfDigits.put(TimeUnit.NANOSECONDS, 3);
	}
	
	/**
	 * This is a shortcut constructor for a MultiUnitTimeFormatter.
	 * The coarsest TimeUnit is HOURS and the finest TimeUnit is MILLISECONDS, so 
	 * 30 Minutes and 5 Seconds would be 0:30:05:000. 
	 * 
	 */
	public MultiUnitTimeFormatter() {
		this(TimeUnit.MILLISECONDS,TimeUnit.HOURS,':');
	}
	/**
	 * This is a constructor for a MultiUnitTimeFormatter.
	 * 30 Minutes and 5 Seconds would be 0:30:05:000 if the coarsest TimeUnit is HOURS
	 * and the finest TimeUnit is MILLISECONDS. It would be 30:05 if the coarsest TimeUnit
	 * is MINUTES and the finest is SECONDS
	 * 
	 * @param coarsestUnit
	 * 					TimeUnit: The coarsest TimeUnit
	 * 
	 * @param finestUnit
	 * 					TimeUnit: The finest TimeUnit
	 */
	public MultiUnitTimeFormatter(TimeUnit coarsestUnit, TimeUnit finestUnit) {
		this(coarsestUnit, finestUnit,':');
	}
	
	/**
	 *Use this constructor for a MultiUnitTimeFormatter to choose a different separator than the
	 *default separator ':'.
	 * 30 Minutes and 5 Seconds would be 0:30:05:000 if the coarsest TimeUnit is HOURS
	 * and the finest TimeUnit is MILLISECONDS. It would be 30:05 if the coarsest TimeUnit
	 * is MINUTES and the finest is SECONDS. 
	 * 
	 * @param coarsestUnit
	 * 					TimeUnit: The coarsest TimeUnit
	 * 
	 * @param finestUnit
	 * 					TimeUnit: The finest TimeUnit
	 * @param separator
	 * 					char: The separator used to separate the TimeUnits.
	 */
	public MultiUnitTimeFormatter(TimeUnit coarsestUnit,TimeUnit finestUnit, char separator){
		if (coarsestUnit.compareTo(finestUnit) < 0) {
			TimeUnit bufferUnit = coarsestUnit;
			coarsestUnit = finestUnit;
			finestUnit = bufferUnit;
		}
		this._unitSet = EnumSet.range(finestUnit, coarsestUnit);
		this._separator=separator;
	}
	
	/**Returns the String-Representation of the given TimeInstant.
	 *  
	 */
	public String buildTimeString(TimeInstant instant) {
		return buildMultiUnitTimeString(instant.getTimeInEpsilon());
	}
	/**Returns the String-Representation of the given TimeSpan.
	 *  
	 */
	public String buildTimeString(TimeSpan span) {
		return buildMultiUnitTimeString(span.getTimeInEpsilon());
	}
	/**Returns the String-Representation of the given time object.
	 *  
	 */
	private String buildMultiUnitTimeString(long timeValue) {
		StringBuffer timeStringBuffer = new StringBuffer();
		TimeUnit eps = TimeOperations.getEpsilon();
		//The Iterator BiggerUnit is used to 
		Iterator<TimeUnit> iteratorBiggerUnit = _unitSet.iterator();
		iteratorBiggerUnit.next();
		for (Iterator<TimeUnit> iteratorUnit = _unitSet.iterator(); iteratorUnit
				.hasNext();) {
			TimeUnit currentUnit = iteratorUnit.next();
			long timeCurrentUnit = currentUnit.convert(timeValue, eps);

			if (iteratorBiggerUnit.hasNext()) {
				//There is a bigger unit, i.e. this is not the biggest unit
				TimeUnit biggerUnit = iteratorBiggerUnit.next();
				long aBiggerUnitAsCurrentUnit = currentUnit.convert(1,
						biggerUnit);
				timeCurrentUnit = timeCurrentUnit % aBiggerUnitAsCurrentUnit;
				StringBuffer unitStringBuffer = new StringBuffer();
				unitStringBuffer.append(_separator);
				String unitString=Long.toString(timeCurrentUnit);
				char zero = '0';
				//append as many zeros as needed
				for (int i = unitString.length(); i < numberOfDigits.get(currentUnit); i++) {
					unitStringBuffer.append(zero);
				}
				unitStringBuffer.append(unitString);
				//insert value of the current unit at the first position
				timeStringBuffer.insert(0, unitStringBuffer);
			} else {
				//This is the biggest unit. insert value of the biggest unit at the first position
				timeStringBuffer.insert(0, timeCurrentUnit);
			}
		}

		return timeStringBuffer.toString();
	}
}
