#include <bits/stdc++.h>
#include "rapidxml.hpp"

using namespace std;
using namespace rapidxml;

class node{
public:
    long long int id;
    double lat;
    double lon;
    string name;
    node(){
        id = 0;
        lat = 0;
        lon = 0;
        name = "";
    }
    node(long long int id, double lat, double lon, string name){
        this->id = id;
        this->lat = lat;
        this->lon = lon;
        this->name = name;
    }
};

xml_document<> doc;
xml_node<> * osm_root = NULL;
   
int main(){

    //name_id_map contains all the pairs of names along with their corresponding ids
    vector<pair<string, long long int>> name_id_map;
    //nodes contains information about all the nodes in the map
    vector<node> nodes;
    //index_map contains the index of the node in the nodes vector
    map<long long int, int> index_map;

    int no_ways=0;


    //READING AND PARSING THE OSM FILE
    cout << "Staring the parsing of the OSM file...\n" << setprecision(10);
    // Read the sample.xml file into a buffer
    ifstream theFile ("map.osm");
    vector<char> buffer((istreambuf_iterator<char>(theFile)), istreambuf_iterator<char>());
    buffer.push_back('\0');
    // Parse the buffer
    doc.parse<0>(&buffer[0]);
   

    // Find out the root node
    osm_root = doc.first_node("osm");
    // Iterate over all the child nodes of the root node and if they are nodes, increase the count of nodes and if they are ways, increase the count of ways
    for (xml_node<> * student_node = osm_root->first_node(); student_node; student_node = student_node->next_sibling()){
        if (student_node->name() == string("node")){
            //if node contains no tags, we add it to the node vector and continue
            if(!student_node->first_node("tag")){
                nodes.push_back(node(stoll(student_node->first_attribute("id")->value()), stod(student_node->first_attribute("lat")->value()), stod(student_node->first_attribute("lon")->value()), ""));
                index_map[stoll(student_node->first_attribute("id")->value())] = nodes.size()-1;
                continue;
            }

            string name = "";
            //if node contains tags, we iterate over them till we find the name tag and add it to the name_mapping
            for(xml_node<> * tag = student_node->first_node("tag"); tag; tag = tag->next_sibling()){
                if(tag->first_attribute("k")->value() == string("name")){
                    name = tag->first_attribute("v")->value();
                }
                if(tag->first_attribute("k")->value() == string("name:en")){
                    name = tag->first_attribute("v")->value();
                    break;
                }
            }

            //We add node to the nodes map while maintaining the original name there
            nodes.push_back(node(stoll(student_node->first_attribute("id")->value()), stod(student_node->first_attribute("lat")->value()), stod(student_node->first_attribute("lon")->value()), name));
            index_map[stoll(student_node->first_attribute("id")->value())] = nodes.size()-1;

            //convert name to lowercase so that we can search without case sensitivity
            transform(name.begin(), name.end(), name.begin(), ::tolower);
            //if name is not empty, we add it to the name_mapping
            if(name != ""){
                name_id_map.push_back(make_pair(name, stoll(student_node->first_attribute("id")->value())));
            }
        }
        else if (student_node->name() == string("way")){
            no_ways++;
        }
    }
    cout<<"Total number of nodes: "<<nodes.size()<<endl;
    cout<<"Total number of ways: "<<no_ways<<endl;




    while(true){
        cout<<"\n\n\n\nWhat do you want to do?\n0. Exit\n1. Search for a place by name\n2. Search for k closest places to a given place\n3. Find shortest path between two places\nEnter your choice: ";
        int choice;
        cin>>choice;
        if(choice<0 || choice>3){
            cout<<"Invalid choice. Please enter a valid choice.\n";
            continue;
        }
        if(choice==0){
            break;
        }
        if(choice==1){
            cout<<"Enter the name you want to search for: ";
            string name;
            cin>>name;
            transform(name.begin(), name.end(), name.begin(), ::tolower);
            int matches_found = 0;
            vector<long long int> matches;
            for(int i=0;i<name_id_map.size();i++){
                if(name_id_map[i].first.find(name) != string::npos){
                    matches_found++;
                    matches.push_back(name_id_map[i].second);
                }
            }
            if(matches_found == 0){
                cout<<"No matches found\n";
            }
            else{
                cout<<"Matches found: "<<matches_found<<endl;
                for(int i=0;i<matches.size();i++){
                    cout<<"Name: "<<nodes[index_map[matches[i]]].name<<endl;
                    cout<<"NodeID: "<<matches[i]<<endl;
                    cout<<"Latitude: "<<nodes[index_map[matches[i]]].lat<<endl;
                    cout<<"Longitude: "<<nodes[index_map[matches[i]]].lon<<endl;
                    cout<<"\n";
                }
            }
        }
    }
    return 0;
}