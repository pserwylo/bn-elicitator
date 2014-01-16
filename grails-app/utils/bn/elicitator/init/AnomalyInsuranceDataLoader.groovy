package bn.elicitator.init
/*
 * Bayesian Network (BN) Elicitator
 * Copyright (C) 2012 Peter Serwylo (peter.serwylo@monash.edu)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

class DevelopmentAnomalyInsuranceDataLoader extends AnomalyInsuranceDataLoader {

	protected void initOther() {
		updateHomePage()
		initTestUsers( 50, false )
	}

}


class AnomalyInsuranceDataLoader extends InsuranceDataLoader {

	/**
	 * These arcs are the result of performing Dawid & Skene on the relationships elicited in the first evaluation
	 * study. There are cycles, and other anomalies, which we will try to resolve.
	 */
	protected List<List<String>> getBnArcs() {

		/*return [
			[ "HomeBase", "OtherCar" ],
			[ "Airbag", "OtherCar" ],
			[ "Antilock", "Airbag" ],
			[ "Antilock", "SeniorTrain" ],
			[ "SeniorTrain", "VehicleYear" ],
			[ "VehicleYear", "Antilock" ],
			[ "OtherCar", "VehicleYear" ],
		]*/

		[
			[ "Antilock", "ThisCarCost" ],
			[ "Antilock", "DrivHist" ],
			[ "SeniorTrain", "OtherCar" ],
			[ "SeniorTrain", "DrivHist" ],
			[ "VehicleYear", "IliCost" ],
			[ "VehicleYear", "Airbag" ],
			[ "SocioEcon", "AntiTheft" ],
			[ "DrivingSkill", "SeniorTrain" ],
			[ "VehicleYear", "Antilock" ],
			[ "VehicleYear", "HomeBase" ],
			[ "VehicleYear", "ThisCarCost" ],
			[ "VehicleYear", "AntiTheft" ],
			[ "VehicleYear", "Theft" ],
			[ "DrivingSkill", "OtherCar" ],
			[ "DrivingSkill", "DrivHist" ],
			[ "DrivingSkill", "DrivQuality" ],
			[ "SocioEcon", "Airbag" ],
			[ "DrivHist", "Theft" ],
			[ "DrivQuality", "Accident" ],
			[ "DrivHist", "SeniorTrain" ],
			[ "DrivHist", "ThisCarCost" ],
			[ "DrivHist", "DrivingSkill" ],
			[ "DrivQuality", "RiskAversion" ],
			[ "DrivHist", "IliCost" ],
			[ "RiskAversion", "DrivHist" ],
			[ "RiskAversion", "Airbag" ],
			[ "RiskAversion", "HomeBase" ],
			[ "RiskAversion", "AntiTheft" ],
			[ "RiskAversion", "SeniorTrain" ],
			[ "RiskAversion", "Antilock" ],
			[ "Mileage", "DrivHist" ],
			[ "MakeModel", "Antilock" ],
			[ "MakeModel", "Mileage" ],
			[ "MakeModel", "Theft" ],
			[ "MakeModel", "AntiTheft" ],
			[ "MakeModel", "ThisCarCost" ],
			[ "DrivHist", "DrivQuality" ],
			[ "SocioEcon", "VehicleYear" ],
			[ "SocioEcon", "MakeModel" ],
			[ "ThisCarCost", "MakeModel" ],
			[ "ThisCarCost", "Age" ],
			[ "Theft", "AntiTheft" ],
			[ "Theft", "HomeBase" ],
			[ "VehicleYear", "Accident" ],
			[ "VehicleYear", "RuggedAuto" ],
			[ "Antilock", "VehicleYear" ],
			[ "RuggedAuto", "ThisCarDam" ],
			[ "MakeModel", "Airbag" ],
			[ "Airbag", "MakeModel" ],
			[ "OtherCar", "Accident" ],
			[ "Airbag", "VehicleYear" ],
			[ "Accident", "DrivHist" ],
			[ "Accident", "IliCost" ],
			[ "MakeModel", "ThisCarDam" ],
			[ "MakeModel", "RuggedAuto" ],
			[ "Accident", "SeniorTrain" ],
			[ "Theft", "IliCost" ],
			[ "OtherCar", "DrivingSkill" ],
			[ "OtherCarCost", "Age" ],
			[ "Age", "MakeModel" ],
			[ "Age", "Accident" ],
			[ "ThisCarDam", "Accident" ],
			[ "DrivQuality", "OtherCar" ],
			[ "Age", "DrivingSkill" ],
			[ "Age", "DrivQuality" ],
			[ "Age", "ThisCarCost" ],
			[ "DrivQuality", "SeniorTrain" ],
			[ "DrivQuality", "DrivingSkill" ],
			[ "Age", "Airbag" ],
			[ "Age", "IliCost" ],
			[ "DrivQuality", "IliCost" ],
			[ "DrivQuality", "DrivHist" ],
			[ "SeniorTrain", "Accident" ],
			[ "SeniorTrain", "ThisCarDam" ],
			[ "DrivingSkill", "RiskAversion" ],
			[ "SeniorTrain", "RiskAversion" ],
			[ "DrivingSkill", "Age" ],
			[ "DrivingSkill", "Accident" ],
			[ "DrivingSkill", "ThisCarDam" ],
			[ "OtherCarCost", "DrivingSkill" ],
			[ "HomeBase", "Theft" ],
			[ "HomeBase", "AntiTheft" ],
			[ "HomeBase", "ThisCarCost" ],
			[ "RiskAversion", "MakeModel" ],
			[ "RiskAversion", "ThisCarDam" ],
			[ "AntiTheft", "ThisCarCost" ],
			[ "ThisCarDam", "IliCost" ],
			[ "AntiTheft", "Theft" ],
			[ "ThisCarDam", "DrivHist" ],
		]
	}

}
