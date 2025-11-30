package com.example.collegecompanion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.collegecompanion.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val items = listOf(
            ActivityItem("Advanced Mathematics", "Room 302 • Lecture", "10:00 AM"),
            ActivityItem("History of Art", "Room 105 • Seminar", "1:00 PM"),
            ActivityItem("Computer Science", "Lab 4 • Practical", "3:00 PM"),
            ActivityItem("Library Study", "Main Library", "5:00 PM")
        )

        val adapter = RecentActivityAdapter(items)
        binding.recyclerRecent.layoutManager = LinearLayoutManager(context)
        binding.recyclerRecent.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

data class ActivityItem(val title: String, val subtitle: String, val time: String)