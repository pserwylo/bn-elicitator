package bn.elicitator.anomalies

abstract interface AnomalyInfo {

	abstract public String getDescription()

	abstract public String getEncodedProblemImage()

	abstract public List<ProposedAnomalySolution> getSolutions()

}

