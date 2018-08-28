package org.mitre.neoprofiler.profiler;

import java.util.List;

import org.mitre.neoprofiler.NeoProfiler;
import org.mitre.neoprofiler.profile.NeoProfile;
import org.mitre.neoprofiler.profile.RelationshipsProfile;

/**
 * Gathers basic summary statistics on relationships, and schedules additional 
 * RelationshipTypeProfilers to run depending on what is found. 
 * @author moxious
 */
public class RelationshipsProfiler extends QueryRunner implements Profiler {
	public NeoProfile run(NeoProfiler parent) {
		RelationshipsProfile p = new RelationshipsProfile();
		List<Object> relTypes = runQueryMultipleResult(parent, "start r=relationship(*) return distinct(type(r)) as relTypes", "relTypes");
		p.addObservation("Available Relationship Types", relTypes);

		for(Object relType : relTypes) {
			String relTypeName = ""+relType;
			if (relTypeName.charAt(0) == '"' && relTypeName.charAt(relTypeName.length() - 1) == '"') {
				relTypeName = relTypeName.substring(1, relTypeName.length() - 1);
			}
			parent.schedule(new RelationshipTypeProfiler(relTypeName));
		}
		
		p.addObservation("Total Relationships", runQuerySingleResult(parent, "start r=relationship(*) return count(r) as c", "c"));
		
		return p;
	}

	public String describe() {
		return "RelationshipsProfiler";
	}
} // End RelationshipsProfiler
