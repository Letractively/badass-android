package com.ensemblevide.badass;

public class BadassEntry {
	
	int mId;
	String mName;
	String mDate;
	String mLink;
	boolean mFavorite;
	boolean mRead;
	int mCode;

	public BadassEntry(Builder pBuilder) {
		mId			= pBuilder.mId;
		mName		= pBuilder.mName;
		mDate		= pBuilder.mDate;
		mLink		= pBuilder.mLink;
		mFavorite	= pBuilder.mFavorite;
		mRead		= pBuilder.mRead;
		mCode		= pBuilder.mCode;
	}
	
	public static class Builder {
		
		// 
		private int		mId;
		private String	mName;
		private String	mDate;
		private String	mLink;
		private int 	mCode;
		
		//
		private boolean	mFavorite	= false;
		private boolean	mRead		= false;
		
		// Public Builder
		public Builder(int pId, String pName, String pDate, String pLink, int pCode) {
			this.mId		= pId;
			this.mName		= pName;
			this.mDate		= pDate;
			this.mLink		= pLink;
			this.mCode		= pCode;
		}
		
		public Builder isFavorite(boolean pFavorite)
			{ this.mFavorite = pFavorite;	return this; }
		public Builder hasBeenRead(boolean pRead)
			{ this.mRead = pRead;	return this; }

		
		public BadassEntry build() 
			{ return new BadassEntry(this); }
	}

	public String toString() {
		StringBuilder string = new StringBuilder();
		
		string.append("### BadassEntry.toString() Start ###");
		string.append("ID: ").append(mId);
		string.append("Name: ").append(mName);
		string.append("Date: ").append(mDate);
		string.append("Link: ").append(mLink);
		string.append("Favorite: ").append(mFavorite);
		string.append("Read: ").append(mRead);
		string.append("### BadassEntry.toString() Stop ###");
		
		return string.toString();
	}

}
