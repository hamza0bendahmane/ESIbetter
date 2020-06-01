package com.example.esibetter.articles;


import androidx.fragment.app.Fragment;

public class Search extends Fragment {


    // <-------- VERY IMPORTANT ----->


    // search feature is nt ready yet so please don't use it .......






    /*
        private RecyclerView recyclerView;
        private FirestoreRecyclerAdapter<Article_item, ArticlesAdapter.ViewHolder> mAdapter;
        private RecyclerView.LayoutManager layoutManager;
        private HashSet<String> cuisineType = new HashSet<>();
        private HashSet<Chip> chips = new HashSet<>();
        private ChipGroup entryChipGroup;
        private boolean icon_pop=false;
        private Menu menu;
        private boolean flag = true,favourites_selected;
        LinkedList<String> keys_favorite_Search;

    public final CollectionReference reference = FirebaseFirestore.getInstance()
            .collection("posts");
    private static int menuClickedPosition = -1;
    final Query query = reference.orderBy("date", Query.Direction.DESCENDING);
    public final FirestoreRecyclerOptions<Article_item> options =
            new FirestoreRecyclerOptions.Builder<Article_item>()
                    .setQuery(query, Article_item.class).build();
    
    
        public Search() {
            // Required empty public constructor
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.ideas_fragment_search, container, false);
            setHasOptionsMenu(true);

            entryChipGroup = view.findViewById(R.id.chip_group);
            recyclerView = view.findViewById(R.id.search_recycler);
            //recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);

            DocumentReference fav_ref = FirebaseFirestore
                    .getInstance().getReference(CUSTOMER_PATH)
                    .child(USER.phone).child(CUSTOMER_FAVOURITE_Search_PATH);
            fav_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    keys_favorite_Search = new LinkedList<>();

                    for(DataSnapshot d : dataSnapshot.getChildren()){
                        keys_favorite_Search.add(d.getKey());
                    }

                    //mAdapter.stopListening();

                    mAdapter = new FirebaseRecyclerAdapter<Article_item, ArticlesAdapter.ViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull Article_item holder, int position, @NonNull Article_item model) {
                            String key = getRef(position).getKey();
                            holder.setIsRecyclable(false);
                            holder.setData(model, position, key);

                        }

                        @NonNull
                        @Override
                        public Article_item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ideas_list_article_item, parent, false);
                            Article_item resViewHolder = new Article_item(view, getContext());
                            resViewHolder.setFavorite(keys_favorite_Search);

                            return resViewHolder;
                        }
                    };
                    recyclerView.setAdapter(mAdapter);
                    mAdapter.startListening();


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




            recyclerView.setAdapter(mAdapter);

            Query query = FirebaseFirestore.getInstance().getReference("posts");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            if(cuisineType.add(d.child("info").child("cuisine").getValue().toString())){
                                Log.d("CHIP", "building");
                                Chip chip = new Chip(view.getContext());
                                chip.setCheckable(true);
                                chip.setText(d.child("info").child("cuisine").getValue().toString());
                                chips.add(chip);
                                entryChipGroup.addView(chip);
                                chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                    if(isChecked && flag) {
                                        setFilter(d.child("info").child("cuisine").getValue().toString());
                                    }
                                });
                            }
                        }
                    }

                    entryChipGroup.setOnCheckedChangeListener((chipGroup, i) -> {
                        if(chipGroup.getCheckedChipId() == View.NO_ID){
                            mAdapter.stopListening();
                            options =
                                    new FirebaseRecyclerOptions.Builder<Article_item>()
                                            .setQuery(FirebaseFirestore.getInstance().getReference("posts"),
                                                    Article_item.class).build();

                            mAdapter = new FirebaseRecyclerAdapter<Article_item, Article_item>(options) {
                                @Override
                                protected void onBindViewHolder(@NonNull Article_item holder, int position, @NonNull Article_item model) {
                                    String key = getRef(position).getKey();
                                    holder.setData(model, position, key);
                                }

                                @NonNull
                                @Override
                                public Article_item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.ideas_list_article_item,parent,false);
                                    Article_item resViewHolder = new Article_item(view1,getContext());
                                    resViewHolder.setFavorite(keys_favorite_Search);

                                    return resViewHolder;
                                }

                            };

                            recyclerView.setAdapter(mAdapter);
                            mAdapter.startListening();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            return view;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.search, menu);
            final MenuItem searchItem = menu.findItem(R.id.search);
            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

            this.menu = menu;
            MenuItem heart = menu.findItem(R.id.favorite_res);
            heart.setOnMenuItemClickListener(e -> {
                favourites_selected = !favourites_selected;
                mAdapter.stopListening();
                if (favourites_selected) {
                    heart.setIcon(R.drawable.heart_fill_white);
                    options = new FirebaseRecyclerOptions.Builder<Article_item>()
                            .setQuery(FirebaseFirestore.getInstance()
                                            .getReference(CUSTOMER_PATH)
                                            .child(USER.phone).child(CUSTOMER_FAVOURITE_Search_PATH),
                                    Article_item.class).build();
                } else {
                    heart.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.heart_white));
                    options =
                            new FirebaseRecyclerOptions.Builder<Article_item>()
                                    .setQuery(FirebaseFirestore.getInstance().getReference("posts"),
                                            Article_item.class).build();
                }
                mAdapter = new FirebaseRecyclerAdapter<Article_item, Article_item>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull Article_item holder, int position, @NonNull Article_item model) {
                        String key = getRef(position).getKey();
                        holder.setData(model, position, key);
                    }

                    @NonNull
                    @Override
                    public Article_item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ideas_list_article_item,parent,false);
                        Article_item resViewHolder = new Article_item(view,getContext());
                        resViewHolder.setFavorite(keys_favorite_Search);

                        return resViewHolder;
                    }
                };
                recyclerView.setAdapter(mAdapter);
                mAdapter.startListening();
                return false;
            });
            MenuItem pop = menu.findItem(R.id.most_popular_res);
            pop.setOnMenuItemClickListener(d->{
                icon_pop=!icon_pop;
                if(icon_pop){
                    pop.setIcon(R.drawable.ic_Search);
                    mAdapter.stopListening();
                    options = new FirebaseRecyclerOptions.Builder<Article_item>()
                            .setQuery(FirebaseFirestore.getInstance()
                                            .getReference("posts")
                                            .orderByChild("stars/sort"),
                                    Article_item.class).build();
                }
                else{
                    pop.setIcon(R.drawable.ic_chart);
                    mAdapter.stopListening();
                    options = new FirebaseRecyclerOptions.Builder<Article_item>()
                            .setQuery(FirebaseFirestore.getInstance()
                                            .getReference("posts"),Article_item.class).build();

                }


                mAdapter = new FirebaseRecyclerAdapter<Article_item, Article_item>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull Article_item holder, int position, @NonNull Article_item model) {
                        String key = getRef(position).getKey();
                        holder.setData(model, position, key);
                    }

                    @NonNull
                    @Override
                    public Article_item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ideas_list_article_item,parent,false);
                        Article_item resViewHolder = new Article_item(view,getContext());
                        resViewHolder.setFavorite(keys_favorite_Search);

                        return resViewHolder;
                    }
                };
                recyclerView.setAdapter(mAdapter);
                mAdapter.startListening();

                return false;
            });

            searchView.setOnCloseListener(() -> {
                entryChipGroup.setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.navigation).setVisibility(View.VISIBLE);
                return false;
            });

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    mAdapter.stopListening();
                    if(newText.length()==0){
                        entryChipGroup.setVisibility(View.GONE);
                        getActivity().findViewById(R.id.navigation).setVisibility(View.GONE);
                        options =
                                new FirebaseRecyclerOptions.Builder<Article_item>()
                                        .setQuery(FirebaseFirestore.getInstance().getReference("posts"),Article_item.class).build();
                    }
                    else {
                        entryChipGroup.setVisibility(View.GONE);
                        getActivity().findViewById(R.id.navigation).setVisibility(View.GONE);
                        options =
                                new FirebaseRecyclerOptions.Builder<Article_item>()
                                        .setQuery(FirebaseFirestore.getInstance().getReference().child("posts"), Article_item.class).build();
                    }
                    mAdapter = new FirebaseRecyclerAdapter<Article_item, Article_item>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull Article_item holder, int position, @NonNull Article_item model) {
                            String key = getRef(position).getKey();
                            if(model.getName().equals("")){
                                holder.itemView.findViewById(R.id.Search).setLayoutParams(new FrameLayout.LayoutParams(0,0));
                                //holder.itemView.setLayoutParams(new ConstraintLayout.LayoutParams(0,0));
                            }
                            else {
                                holder.setData(model, position, key);
                            }
                        }

                        @NonNull
                        @Override
                        public Article_item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ideas_list_article_item,parent,false);
                            return new Article_item(view,getContext());
                        }
                    };
                    recyclerView.setAdapter(mAdapter);
                    mAdapter.startListening();
                    return false;
                }
            });




            super.onCreateOptionsMenu(menu, inflater);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            switch(id){
                case R.id.search:
                    return true;

                default:
                    return super.onOptionsItemSelected(item);
            }
        }

        private void setFilter(String filter){
            mAdapter.stopListening();

            FirebaseRecyclerOptions<Article_item> options = new FirebaseRecyclerOptions.Builder<Article_item>()
                    .setQuery(FirebaseFirestore.getInstance().getReference().child("posts"), Article_item.class).build();

            mAdapter = new FirestoreRecyclerAdapter<Article_item, Article_item>(options) {
                @Override
                protected void onBindViewHolder(@NonNull Article_item holder, int position, @NonNull Article_item model) {
                    String key = getRef(position).getKey();
                    if(model.getName().equals("")){
                        holder.itemView.findViewById(R.id.Search).setLayoutParams(new FrameLayout.LayoutParams(0,0));
                        //holder.itemView.setLayoutParams(new ConstraintLayout.LayoutParams(0,0));
                    }
                    else {
                        holder.setData(model, position, key);
                    }
                }

                @NonNull
                @Override
                public Article_item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ideas_list_article_item,parent,false);
                    return new Article_item(view,getContext());
                }
            };
            recyclerView.setAdapter(mAdapter);
            mAdapter.startListening();
        }

        @Override
        public void onStart() {
            super.onStart();
            if(mAdapter!=null) {
                mAdapter.startListening();
            }

        }
        @Override
        public void onStop() {
            super.onStop();
            mAdapter.stopListening();
        }

        @Override
        public void onResume() {
            super.onResume();

        }

        public interface OnFragmentInteractionListener {
        }


     */
}








