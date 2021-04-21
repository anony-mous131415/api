package io.revx.api.utils;

import java.util.ArrayList;
import java.util.List;

public class ArrayOfJSON {

	private List<JSONObject> objects = new ArrayList<JSONObject>();

	public List<JSONObject> getObjects() {
		return objects;
	}

	public void setObjects(List<JSONObject> objects) {
		this.objects = objects;
	}

	public void addObject(JSONObject object) {
		this.objects.add(object);
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder("[");
		for (JSONObject object : objects) {
			str.append(object.toString()).append(",");
		}
		str.setLength(str.length() - 1);
		str.append("]");
		return str.toString();
	}
	
	public ArrayOfJSON buildJSONFromString(String text, String separator) {
		String[] lines = text.split("\n");

		String columns[] = lines[0].split(separator);

		ArrayOfJSON array = new ArrayOfJSON();
		for (int i = 1; i < lines.length; i++) {
			JSONObject json = new JSONObject();
			String values[] = lines[i].split(separator);

			for (int j = 0; j < values.length; j++) {
				JSONPair pair = new JSONPair(columns[j], '\"'+values[j]+'\"');
				json.addPair(pair);
			}
			array.addObject(json);
		}
		return array;
	}

}
