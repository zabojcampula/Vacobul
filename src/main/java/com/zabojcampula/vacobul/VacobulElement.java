package com.zabojcampula.vacobul;

import java.time.Instant;

class VacobulElement {
	public String enWord;
	public String czWord;
	public int bucketno;
	public int probability;
	public String examples;
	public Instant created;
	public Instant lastChecked;

	public String getEnWord() {
		return enWord;
	}

	// needed for jackson
	public VacobulElement() {
	}


	public VacobulElement(String en, String cz, String examples) {
		enWord = en;
		czWord = cz;
		bucketno = 1;
		probability = 8;
		this.examples = examples;
		created = Instant.now();
		lastChecked = created;
	}

	public VacobulElement(String en, String cz, int bucketno_, int probability_, String examples_,
						  Instant created_, Instant lastChecked_) {
		enWord = en;
		czWord = cz;
		bucketno = bucketno_;
		probability = probability_;
		examples = examples_;
		created = created_;
		lastChecked = lastChecked_;
	}

	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append(enWord);      b.append(", ");
		b.append(czWord);	   b.append(", ");
		b.append(bucketno);    b.append(", ");
		b.append(probability); b.append(", ");
		b.append(examples);    b.append(", ");
		b.append(created);     b.append(", ");
		b.append(lastChecked);
		return b.toString();
	}

}
